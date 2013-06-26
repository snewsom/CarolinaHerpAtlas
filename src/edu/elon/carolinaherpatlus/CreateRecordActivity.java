/**
 * THe main activ
 * 
 * @author J. Hollingsworth, David Belyea, and Spencer Newsom
 */

package edu.elon.carolinaherpatlus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint("NewApi")
public class CreateRecordActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */

	String DBURL;
	ViewPager mViewPager;
	Bundle recordBundle;

	ArrayList<String> ncCounties;
	ArrayList<String> scCounties;
	JSONObject json;
	String jsonString;
	File holdingFolder;
	Context context;
	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_record);

		// Set up the action bar.
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of
		// the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the
		// corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if
		// we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {

						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the
		// action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title
			// defined by
			// the adapter. Also specify this Activity object, which
			// implements
			// the TabListener interface, as the callback (listener)
			// for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));

		}
		// record bundle temporarily holds all of the values
		// for the record as a hashmap
		recordBundle = new Bundle();
		// provides formating for jsonstring
		json = new JSONObject();
		holdingFolder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + R.string.file_path);
		holdingFolder.mkdirs();
		context = this.getBaseContext();
		// url to point the post information
		DBURL = getResources().getString(R.string.database_url);

	}

	@Override
	protected void onResume() {
		//this resets the view to the first fragment in the list
		//this prevents null pointers of the activity trying to call fragments
		//that have not been loaded yet.
		//TODO fix that ^
		actionBar.setSelectedNavigationItem(0);
		submitHeldFiles();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it
		// is present.
		getMenuInflater().inflate(R.menu.activity_create_record, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_submit_button:
			submitRecord();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


	/**
	 * Creates record to post.
	 */
	private void submitRecord() {

		json = new JSONObject();
		//collect all of the keys from the map to format to JSON
		Set<String> keys = recordBundle.keySet();
		for (String key : keys) {
			try {
				json.put(key, recordBundle.getString(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		Log.d("record", json.toString());
		jsonString = json.toString();
		String filePath = createFilePath();
		File file = new File(filePath);
		try {
			file.createNewFile();

			FileOutputStream outputStream = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(outputStream);
			outWriter.append(jsonString);
			outWriter.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		new PostRecord().execute(DBURL, jsonString, file.getAbsolutePath());
	}

	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

	/**
	 * Submits files held when attempted to submit without Internet connection
	 */
	private void submitHeldFiles() {
		File[] files = holdingFolder.listFiles();
		for (File inFile : files) {
			if (!inFile.isDirectory()) {
				try {
					FileInputStream fin = new FileInputStream(inFile);
					jsonString = convertStreamToString(fin);
					fin.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				new PostRecord().execute(DBURL, jsonString,
						inFile.getAbsolutePath());

			}
		}

	}

	private String createFilePath() {

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1; // The
												// reason
												// the
												// month is
												// off by 1
												// is
												// because
												// January
												// is 0.
		int day = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		int seconds = c.get(Calendar.SECOND);

		String dataPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/CarolinaHerpAtlas/"
				+ month
				+ "-"
				+ day
				+ "-"
				+ year
				+ " "
				+ hour
				+ "h "
				+ minutes
				+ "m "
				+ seconds
				+ "s" + ".json";

		return dataPath;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		Log.d("select", "Tab selected");
		// When the given tab is selected, switch to the corresponding
		// page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,

	FragmentTransaction fragmentTransaction) {
		Log.d("Unselect", "Tab Unselected");
		int tabpos = tab.getPosition();
		// create object of last tab
		DummySectionFragment frag = (DummySectionFragment) mViewPager
				.getAdapter().instantiateItem(mViewPager, tabpos);
		Log.d("Tabpos", "" + tabpos);
		switch (tabpos) {
		case 0:
			ImageView imageView = (ImageView) frag.getView().findViewById(
					R.id.imageView);
			Bundle imageBundle = frag.getArguments();
			recordBundle.putString(getResources().getString(R.string.image_path), frag.getImagePath());
			// String encodedImage = Base64.encodeToString(, flags);

			break;
		case 1:
			Spinner groupSpinner = (Spinner) frag.getView().findViewById(
					R.id.spinnerGroup);
			Spinner genusSpinner = (Spinner) frag.getView().findViewById(
					R.id.spinnerGenus);
			Spinner speciesSpinner = (Spinner) frag.getView().findViewById(
					R.id.spinnerSpecies);

			recordBundle.putString(getResources().getString(R.string.group_label), groupSpinner.getSelectedItem()
					.toString());
			recordBundle.putString(getResources().getString(R.string.genus_label), genusSpinner.getSelectedItem()
					.toString());
			recordBundle.putString(getResources().getString(R.string.species_label), speciesSpinner.getSelectedItem()
					.toString());
			break;

		case 2:
			TimePicker time = (TimePicker) frag.getView().findViewById(
					R.id.timePicker);
			DatePicker datePicked = (DatePicker) frag.getView().findViewById(
					R.id.datePicker);

			recordBundle.putString("Time", "Hour: " + time.getCurrentHour()
					+ " Minute: " + time.getCurrentMinute());
			recordBundle.putString(
					"DatePicked",
					"Month: " + datePicked.getMonth() + " Day: "
							+ datePicked.getDayOfMonth() + " Year: "
							+ datePicked.getYear());
			break;

		case 3:
			EditText UTMEast = (EditText) frag.getView().findViewById(
					R.id.eastEdit);
			EditText UTMNorth = (EditText) frag.getView().findViewById(
					R.id.northEdit);
			EditText UTMZone = (EditText) frag.getView().findViewById(
					R.id.zoneEdit);
			EditText locationEdit = (EditText) frag.getView().findViewById(
					R.id.locationEdit);

			recordBundle.putString(getResources().getString(R.string.UTMEast_label), UTMEast.getText().toString());
			recordBundle.putString(getResources().getString(R.string.UTMNorth_label), UTMNorth.getText().toString());
			recordBundle.putString(getResources().getString(R.string.UTMZone_label), UTMZone.getText().toString());
			recordBundle.putString(getResources().getString(R.string.location_comment_label), locationEdit.getText()
					.toString());
			break;
		case 4:
			EditText comments = (EditText) frag.getView().findViewById(
					R.id.commentText);
			recordBundle.putString(getResources().getString(R.string.comments_label), comments.getText().toString());
			break;
		case 5:
			AutoCompleteTextView countyText = (AutoCompleteTextView) frag
					.getView().findViewById(R.id.autocomplete_county);
			recordBundle.putString(getResources().getString(R.string.county_label), countyText.getText().toString());
			break;
		default:

			break;
		}
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {

			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			// getItem is called to instantiate the fragment for the
			// given page.
			// Return a DummySectionFragment (defined as a static
			// inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {

			// Show 6 total pages.
			return 6;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			switch (position) {
			case 0:
				return getString(R.string.photo_section).toUpperCase();
			case 1:
				return getString(R.string.group_section).toUpperCase();
			case 2:
				return getString(R.string.time_section).toUpperCase();
			case 3:
				return getString(R.string.location_section).toUpperCase();
			case 4:
				return getString(R.string.comments_section).toUpperCase();
			case 5:
				return getString(R.string.county_section).toUpperCase();
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {

		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */

		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			// uses the position of the tab to determine which
			// fragment to
			// inflate
			int position = getArguments().getInt(ARG_SECTION_NUMBER);

			// Change returns to the view id's of fragments
			switch (position) {
			case 0:
				return buildPhotoFragment(inflater, container,
						savedInstanceState);
			case 1:
				return buildGroupFragment(inflater, container,
						savedInstanceState);
			case 2:
				return buildTimeFragment(inflater, container,
						savedInstanceState);
			case 3:
				return buildLocFragment(inflater, container, savedInstanceState);
			case 4:
				return buildCommentsFragment(inflater, container,
						savedInstanceState);
			case 5:
				return buildCountyFragment(inflater, container,
						savedInstanceState);
			}
			return null;
		}

		String[] counties;
		ArrayAdapter<String> adapter;
		AutoCompleteTextView autoText;

		private View buildCountyFragment(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {

			View view = inflater.inflate(R.layout.countyfragment, container,
					false);

			final RadioGroup stateRadioGroup = (RadioGroup) view
					.findViewById(R.id.radioGroup);

			counties = getResources().getStringArray(R.array.nc_counties);

			autoText = (AutoCompleteTextView) view
					.findViewById(R.id.autocomplete_county);

			stateRadioGroup
					.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {

							if (stateRadioGroup.getCheckedRadioButtonId() == R.id.radioNorth) {
								counties = getResources().getStringArray(
										R.array.nc_counties);
								Toast.makeText(getActivity(), "NC",
										Toast.LENGTH_SHORT).show();
								adapter = new ArrayAdapter<String>(
										getActivity(),
										android.R.layout.simple_dropdown_item_1line,
										counties);
								autoText.setAdapter(adapter);
							}
							if (stateRadioGroup.getCheckedRadioButtonId() == R.id.radioSouth) {
								counties = getResources().getStringArray(
										R.array.sc_counties);
								Toast.makeText(getActivity(), "SC",
										Toast.LENGTH_SHORT).show();
								adapter = new ArrayAdapter<String>(
										getActivity(),
										android.R.layout.simple_dropdown_item_1line,
										counties);
								autoText.setAdapter(adapter);
							}
						}
					});

			adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_dropdown_item_1line, counties);
			autoText.setAdapter(adapter);

			autoText.setThreshold(1);

			view.clearFocus();
			return view;
		}

		List<String> group;
		List<String> genus;
		List<String> species;
		String commonName;
		ArrayAdapter<String> groupAdapter;
		ArrayAdapter<String> genusAdapter;
		ArrayAdapter<String> speciesAdapter;

		int groupSpot = 0;
		int genusSpot = 0;
		int speciesSpot = 0;

		TextView commonNameView;

		private View buildGroupFragment(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {

			View view = inflater.inflate(R.layout.groupfragment, container,
					false);

			group = new ArrayList<String>();
			genus = new ArrayList<String>();
			species = new ArrayList<String>();
			commonName = "";

			buildGroups();
			buildGenus(group.get(groupSpot));
			buildSpecies(group.get(groupSpot), genus.get(genusSpot));
			buildCommon(group.get(groupSpot), genus.get(genusSpot),
					species.get(speciesSpot));

			Log.d("grouplist", group.toString());
			Log.d("genuslist", genus.toString());
			Log.d("specieslist", species.toString());
			Log.d("commonNamelist", commonName.toString());

			final Spinner groupSpinner = (Spinner) view
					.findViewById(R.id.spinnerGroup);
			final Spinner genusSpinner = (Spinner) view
					.findViewById(R.id.spinnerGenus);
			final Spinner speciesSpinner = (Spinner) view
					.findViewById(R.id.spinnerSpecies);

			groupAdapter = new ArrayAdapter<String>(this.getActivity(),
					android.R.layout.simple_spinner_item, group);
			groupAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			groupSpinner.setAdapter(groupAdapter);

			genusAdapter = new ArrayAdapter<String>(this.getActivity(),
					android.R.layout.simple_spinner_item, genus);
			genusAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			genusSpinner.setAdapter(genusAdapter);

			speciesAdapter = new ArrayAdapter<String>(this.getActivity(),
					android.R.layout.simple_spinner_item, species);
			speciesAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			speciesSpinner.setAdapter(speciesAdapter);

			commonNameView = (TextView) view.findViewById(R.id.commonNameView);

			commonNameView.setText(commonName);

			groupSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int pos, long id) {

							groupSpot = pos;

							genusSpot = 0;
							speciesSpot = 0;

							buildGenus(group.get(groupSpot));
							buildSpecies(group.get(groupSpot),
									genus.get(genusSpot));
							buildCommon(group.get(groupSpot),
									genus.get(genusSpot),
									species.get(speciesSpot));

							genusAdapter.notifyDataSetChanged();
							speciesAdapter.notifyDataSetChanged();

							genusSpinner.setSelection(genusSpot);
							speciesSpinner.setSelection(speciesSpot);

							commonNameView.setText(commonName);
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});

			genusSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int pos, long id) {

							genusSpot = pos;

							speciesSpot = 0;
							buildGenus(group.get(groupSpot));
							buildSpecies(group.get(groupSpot),
									genus.get(genusSpot));
							buildCommon(group.get(groupSpot),
									genus.get(genusSpot),
									species.get(speciesSpot));

							speciesAdapter.notifyDataSetChanged();

							speciesSpinner.setSelection(speciesSpot);

							commonNameView.setText(commonName);
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});

			speciesSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int pos, long id) {

							speciesSpot = pos;

							buildCommon(group.get(groupSpot),
									genus.get(genusSpot),
									species.get(speciesSpot));

							commonNameView.setText(commonName);
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});

			return view;
		}

		private View buildTimeFragment(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {

			View view = inflater.inflate(R.layout.timefragment, container,
					false);

			TimePicker timePicker = (TimePicker) view
					.findViewById(R.id.timePicker);
			DatePicker datePicker = (DatePicker) view
					.findViewById(R.id.datePicker);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				datePicker.setCalendarViewShown(false);
			}
			Calendar cal = Calendar.getInstance();

			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);

			datePicker.updateDate(year, month, day);
			timePicker.setCurrentHour(hour);
			timePicker.setCurrentMinute(min);

			return view;
		}

		LocationManager locManager;
		EditText eastEditText;
		EditText northEditText;
		EditText zoneEditText;
		Button locationButton;
		LocationListener locationListener;

		private View buildLocFragment(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {

			View view = inflater.inflate(R.layout.locationfragment, container,
					false);

			locManager = (LocationManager) view.getContext().getSystemService(
					Context.LOCATION_SERVICE);

			locationButton = (Button) view.findViewById(R.id.locationButton);

			eastEditText = (EditText) view.findViewById(R.id.eastEdit);
			northEditText = (EditText) view.findViewById(R.id.northEdit);
			zoneEditText = (EditText) view.findViewById(R.id.zoneEdit);

			locationButton.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {

					locationListener = new LocationListener() {
						// int count = 0;

						@Override
						public void onLocationChanged(Location location) {
							// count++;
							// if(30 >= count) {
							// locManager.removeUpdates(this);
							// locationButton.setText("Get my location");
							// }
							// if (location.getAccuracy() < 20.0) {
							CoordinateConversion converter = new CoordinateConversion();
							String result = converter.latLon2UTM(
									location.getLatitude(),
									location.getLongitude());
							String[] locationArray = result.split(" ");
							zoneEditText.setText(locationArray[0]);
							eastEditText.setText(locationArray[2]);
							northEditText.setText(locationArray[3]);

							locationButton.setText("Get my location");
							locManager.removeUpdates(this);
							// } else {
							// locationButton.setText("Finding Location...");
							// }
							// Log.d("location", "" + count++);

						}

						@Override
						public void onProviderDisabled(String provider) {

						}

						@Override
						public void onProviderEnabled(String provider) {

						}

						@Override
						public void onStatusChanged(String provider,
								int status, Bundle extras) {

						}

					};
					locManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 0, 0,
							locationListener);

				}

			});

			return view;
		}

		private View buildCommentsFragment(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.commentsfragment, container,
					false);

			return view;

		}

		Button galleryButton;
		Button cameraButton;
		ImageView imageView;

		final int SELECT_PHOTO = 100;

		final int TAKE_PICTURE = 115;

		Uri imageUri;
		boolean hasImage = false;
		Bitmap photo;

		private View buildPhotoFragment(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.photofragment, container,
					false);

			galleryButton = (Button) view.findViewById(R.id.gallerybutton);
			cameraButton = (Button) view.findViewById(R.id.camerabutton);
			imageView = (ImageView) view.findViewById(R.id.imageView);

			galleryButton.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/*");

					startActivityForResult(photoPickerIntent, SELECT_PHOTO);

				}

			});

			cameraButton.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(
							"android.media.action.IMAGE_CAPTURE");
					startActivityForResult(intent, TAKE_PICTURE);

				}

			});
			if (hasImage) {
				imageView.setImageBitmap(photo);
			}

			return view;

		}

		Uri selectedImage;

		public String getImagePath() {
			if (selectedImage != null) {
				return getRealPathFromURI(selectedImage);
			}

			return "";
		}

		private String getRealPathFromURI(Uri contentURI) {
			Cursor cursor = getActivity().getContentResolver().query(
					contentURI, null, null, null, null);
			cursor.moveToFirst();
			int idx = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(idx);
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			super.onActivityResult(requestCode, resultCode, data);

			if (requestCode == SELECT_PHOTO) {
				if (resultCode == RESULT_OK) {
					selectedImage = data.getData();
					InputStream imageStream;
					try {
						imageStream = getActivity().getContentResolver()
								.openInputStream(selectedImage);
						BitmapFactory.Options options = new BitmapFactory.Options();
						// options.inJustDecodeBounds = true;
						options.inSampleSize = 3;
						photo = BitmapFactory.decodeStream(imageStream,
								new Rect(-1, -1, -1, -1), options);
						if (photo.getWidth() > photo.getHeight()) {
							Matrix matrix = new Matrix();
							matrix.postRotate(90);
							Bitmap rotated = Bitmap.createBitmap(photo, 0, 0,
									photo.getWidth(), photo.getHeight(),
									matrix, true);
							imageView.setImageBitmap(rotated);
						} else {
							imageView.setImageBitmap(photo);
						}
						hasImage = true;
						// Bundle bundle = new Bundle();
						// bundle.putString("ImagePath",
						// getRealPathFromURI(selectedImage));
						// this.setArguments(bundle);
						// Log.d("Gallery",getRealPathFromURI(selectedImage));

					} catch (FileNotFoundException e) {
						Log.d("Gallery", "File not Found Gallery");
						e.printStackTrace();
					}

				}
			}
			if (requestCode == TAKE_PICTURE) {
				if (resultCode == Activity.RESULT_OK) {
					selectedImage = data.getData();
					try {
						ContentResolver contentResolver = getActivity()
								.getContentResolver();
						InputStream is = contentResolver
								.openInputStream(selectedImage);
						photo = BitmapFactory.decodeStream(is);
						Log.d("picture",
								photo.getWidth() + " " + photo.getHeight());
						imageView.setImageBitmap(photo);
						hasImage = true;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					// Bundle bundle = new Bundle();
					// bundle.putString("ImagePath",
					// getRealPathFromURI(selectedImage));
					// this.setArguments(bundle);
					// Log.d("Camera",getRealPathFromURI(selectedImage));
				}
			}
		}

		private void buildGroups() {

			InputStream inputStream = getResources()
					.openRawResource(R.raw.json);

			InputStreamReader inputreader = new InputStreamReader(inputStream);
			BufferedReader buffreader = new BufferedReader(inputreader);
			JsonReader jsonreader = new JsonReader(buffreader);

			group.clear();
			String name = "";

			try {
				// begin the full array of entries
				jsonreader.beginArray();
				while (jsonreader.hasNext()) {
					// start of an object inside full array
					// Log.d("JSON", "Start Array Loop");
					jsonreader.beginObject();
					while (jsonreader.hasNext()) {
						// Log.d("JSON", "Start Object Loop");
						// reads the key
						name = jsonreader.nextName();
						if (name.equals("Group")) {
							// reads the value
							name = jsonreader.nextString();
							if (group.contains(name) == false) {
								// Log.d("Added group", name);
								group.add(name);
							}
						} else if (name.equals("Genus")) {
							// reads the value
							name = jsonreader.nextString();
							if (genus.contains(name) == false) {
								// Log.d("Added genus", name);
								genus.add(name);
							}
						} else if (name.equals("Species")) {
							// reads the value
							name = jsonreader.nextString();
							if (species.contains(name) == false) {
								// Log.d("Added species", name);
								species.add(name);
							}
						} else if (name.equals("Common")) {
							// reads the value
							name = jsonreader.nextString();
							if (commonName.contains(name) == false) {
								// Log.d("Added common", name);
								commonName = name;
							}
						} else {
							// Log.d("JSON", "Skip");
							jsonreader.skipValue();
						}
					}
					jsonreader.endObject();
					// Log.d("Group array", group.toString());
					// Log.d("JSON", "Completed Loop");
				}
				// for(int i = 0; group.size() > i; i++) {
				// //Log.d("tag", group.get(i));
				// }
				jsonreader.endArray();
				jsonreader.close();

			} catch (IOException e) {
				Log.d("IO fail", "FAIL");
				e.printStackTrace();
			}
			/*
			 * try { jsonreader.beginArray(); while (jsonreader.hasNext()){
			 * //group array name jsonreader.beginObject(); groupName =
			 * jsonreader.nextString();
			 * 
			 * //start the array of the genus's in the group
			 * jsonreader.beginArray(); while(jsonreader.hasNext()) {
			 * jsonreader.beginObject(); genusName = jsonreader.nextString();
			 * //start collecting the species and common names from the genus
			 * array jsonreader.beginObject(); while(jsonreader.hasNext()) {
			 * //consumes the name of the json object name =
			 * jsonreader.nextName(); speciesName = jsonreader.nextString();
			 * jsonreader.nextName(); commonName = jsonreader.nextString();
			 * tempArray.add(speciesName + "," + commonName);
			 * Log.d("JSON",speciesName + " " + commonName); }
			 * jsonreader.close(); } jsonreader.close(); } jsonreader.close();
			 * 
			 * } catch (IOException e) { e.printStackTrace(); }
			 */

		}

		private void buildGenus(String groupName) {

			InputStream inputStream = getResources()
					.openRawResource(R.raw.json);

			InputStreamReader inputreader = new InputStreamReader(inputStream);
			BufferedReader buffreader = new BufferedReader(inputreader);
			JsonReader jsonreader = new JsonReader(buffreader);

			genus.clear();
			String name = "";

			try {
				// begin the full array of entries
				jsonreader.beginArray();
				while (jsonreader.hasNext()) {
					// start of an object inside full array
					jsonreader.beginObject();
					while (jsonreader.hasNext()) {
						// reads the key
						name = jsonreader.nextName();
						if (name.equals("Group")) {
							name = jsonreader.nextString();
							// checks to see if the group name of the
							// previous
							// list
							// matches the object group name
							if (name.equals(groupName)) {
								name = jsonreader.nextName();
								if (name.equals("Genus")) {
									name = jsonreader.nextString();
									if (genus.contains(name) == false) {
										genus.add(name);
									} else if (name.equals("Species")) {
										name = jsonreader.nextString();
										if (species.contains(name) == false) {
											species.add(name);
										} else if (name.equals("Common")) {
											name = jsonreader.nextString();
											if (commonName.contains(name) == false) {
												commonName = name;
											}
										}
									}
								}
							} else {
								jsonreader.skipValue();
								jsonreader.skipValue();
							}
						} else {
							jsonreader.skipValue();
						}
					}
					jsonreader.endObject();
				}
				jsonreader.endArray();
				jsonreader.close();

			} catch (IOException e) {
				Log.d("IO fail", "FAIL");
				e.printStackTrace();
			}
		}

		private void buildSpecies(String groupName, String genusName) {

			InputStream inputStream = getResources()
					.openRawResource(R.raw.json);

			InputStreamReader inputreader = new InputStreamReader(inputStream);
			BufferedReader buffreader = new BufferedReader(inputreader);
			JsonReader jsonreader = new JsonReader(buffreader);

			species.clear();
			String name = "";

			try {
				// begin the full array of entries
				jsonreader.beginArray();
				while (jsonreader.hasNext()) {
					// start of an object inside full array
					jsonreader.beginObject();
					while (jsonreader.hasNext()) {
						// reads the key
						name = jsonreader.nextName();
						if (name.equals("Group")) {
							name = jsonreader.nextString();
							// checks to see if the group name of the
							// previous
							// list
							// matches the object group name
							if (name.equals(groupName)) {
								name = jsonreader.nextName();
								if (name.equals("Genus")) {
									name = jsonreader.nextString();
									if (name.equals(genusName)) {
										name = jsonreader.nextName();
										if (name.equals("Species")) {
											name = jsonreader.nextString();
											if (species.contains(name) == false) {
												species.add(name);
											} else if (name.equals("Common")) {
												name = jsonreader.nextString();
												if (commonName.contains(name) == false) {
													commonName = name;
												}
											}
										}
									} else {
										// double skip to get a name
										// value
										// instead
										// of a string value
										jsonreader.skipValue();
										jsonreader.skipValue();
									}
								}
							} else {
								jsonreader.skipValue();
								jsonreader.skipValue();
							}
						} else {
							jsonreader.skipValue();
						}

					}

					jsonreader.endObject();
				}
				jsonreader.endArray();
				jsonreader.close();
			} catch (IOException e) {
				Log.d("IO fail", "FAIL");
				e.printStackTrace();
			}
		}

		private void buildCommon(String groupName, String genusName,
				String speciesName) {

			InputStream inputStream = getResources()
					.openRawResource(R.raw.json);

			InputStreamReader inputreader = new InputStreamReader(inputStream);
			BufferedReader buffreader = new BufferedReader(inputreader);
			JsonReader jsonreader = new JsonReader(buffreader);

			commonName = "";
			String name = "";

			try {
				jsonreader.beginArray();
				while (jsonreader.hasNext()) {
					jsonreader.beginObject();
					while (jsonreader.hasNext()) {
						name = jsonreader.nextName();
						if (name.equals("Group")) {
							name = jsonreader.nextString();
							// check if group name is matches previous
							// list
							if (name.equals(groupName)) {

								name = jsonreader.nextName();
								if (name.equals("Genus")) {
									name = jsonreader.nextString();
									// check is genus name matches
									// previous list
									if (name.equals(genusName)) {
										name = jsonreader.nextName();
										if (name.equals("Species")) {
											name = jsonreader.nextString();
											// check if species name
											// matches
											// previous list
											if (name.equals(speciesName)) {
												name = jsonreader.nextName();
												if (name.equals("Common")) {
													name = jsonreader
															.nextString();
													if (commonName
															.contains(name) == false) {
														commonName = name;
													}
												}
											} else {
												jsonreader.skipValue();
												jsonreader.skipValue();
											}
										}
									}
								} else {
									jsonreader.skipValue();
									jsonreader.skipValue();
								}
							} else {
								jsonreader.skipValue();
								jsonreader.skipValue();
							}
						} else {
							jsonreader.skipValue();
						}
					}
					jsonreader.endObject();

				}
				// for(int i = 0; group.size() > i; i++) {
				// //Log.d("tag", group.get(i));
				// }
				jsonreader.endArray();
				jsonreader.close();

			} catch (IOException e) {
				Log.d("IO fail", "FAIL");
				e.printStackTrace();
			}
		}
	}

	public boolean isOnline() {

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

}