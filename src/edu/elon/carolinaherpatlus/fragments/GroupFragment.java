package edu.elon.carolinaherpatlus.fragments;

import java.util.ArrayList;
import java.util.List;

import edu.elon.carolinaherpatlus.R;
import edu.elon.carolinaherpatlus.SpeciesReader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class GroupFragment extends Fragment {

	
	private ArrayAdapter<String> groupAdapter;
	private ArrayAdapter<String> genusAdapter;
	private ArrayAdapter<String> speciesAdapter;

	private int groupSpot = 0;
	private int genusSpot = 0;
	private int speciesSpot = 0;

	private TextView commonNameView;
	
	private SpeciesReader reader;
	
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.groupfragment, container,
				false);

		reader = new SpeciesReader();

//		buildGroups();
//		buildGenus(group.get(groupSpot));
//		buildSpecies(group.get(groupSpot), genus.get(genusSpot));
//		buildCommon(group.get(groupSpot), genus.get(genusSpot),
//				species.get(speciesSpot));
//
//		Log.d("grouplist", group.toString());
//		Log.d("genuslist", genus.toString());
//		Log.d("specieslist", species.toString());
//		Log.d("commonNamelist", commonName.toString());

		final Spinner groupSpinner = (Spinner) view
				.findViewById(R.id.spinnerGroup);
		final Spinner genusSpinner = (Spinner) view
				.findViewById(R.id.spinnerGenus);
		final Spinner speciesSpinner = (Spinner) view
				.findViewById(R.id.spinnerSpecies);

		groupAdapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, reader.getGroup());
		groupAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		groupSpinner.setAdapter(groupAdapter);

		genusAdapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, reader.getGenus(groupSpot));
		genusAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genusSpinner.setAdapter(genusAdapter);

		speciesAdapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, reader.getSpecies(groupSpot, genusSpot));
		speciesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		speciesSpinner.setAdapter(speciesAdapter);

		commonNameView = (TextView) view.findViewById(R.id.commonNameView);

		commonNameView.setText(reader.getCommonName(groupSpot, genusSpot, speciesSpot));

		groupSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0,
							View arg1, int pos, long id) {

						groupSpot = pos;

						genusSpot = 0;
						speciesSpot = 0;
						
						reader.getGroup();

//						buildGenus(group.get(groupSpot));
//						buildSpecies(group.get(groupSpot),
//								genus.get(genusSpot));
//						buildCommon(group.get(groupSpot),
//								genus.get(genusSpot),
//								species.get(speciesSpot));

						genusAdapter.notifyDataSetChanged();
						speciesAdapter.notifyDataSetChanged();

						genusSpinner.setSelection(genusSpot);
						speciesSpinner.setSelection(speciesSpot);

						commonNameView.setText(reader.getCommonName(groupSpot, genusSpot, pos));
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
						reader.updateGenus(reader.getGroup().get(groupSpot));
//						buildGenus(group.get(groupSpot));
//						buildSpecies(group.get(groupSpot),
//								genus.get(genusSpot));
//						buildCommon(group.get(groupSpot),
//								genus.get(genusSpot),
//								species.get(speciesSpot));

						speciesAdapter.notifyDataSetChanged();

						speciesSpinner.setSelection(speciesSpot);

						commonNameView.setText(reader.getCommonName(groupSpot, genusSpot, pos));
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
						
						reader.updateSpecies(reader.getGroup().get(groupSpot), reader.getGenus(groupSpot).get(genusSpot));

//						buildCommon(group.get(groupSpot),
//								genus.get(genusSpot),
//								species.get(speciesSpot));
//
//						commonNameView.setText(commonName);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		return view;
	}
}
