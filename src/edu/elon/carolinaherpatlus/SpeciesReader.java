package edu.elon.carolinaherpatlus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.util.JsonReader;
import android.util.Log;

/**
 * @author snewsom
 * 
 */
public class SpeciesReader {
	// InputStream inputStream = Resources.getSystem()
	// .openRawResource(R.raw.json);

	private List<String> group;
	private List<String> genus;
	private List<String> species;
	private String commonName;

	public SpeciesReader() {
		group = new ArrayList<String>();
		genus = new ArrayList<String>();
		species = new ArrayList<String>();
		commonName = "";
	}

	public List<String> getGroup() {
		updateGroup();
		return group;
	}

	public List<String> getGenus(int groupSpot) {
		updateGenus(group.get(groupSpot));
		return genus;
	}

	public List<String> getSpecies(int groupSpot, int genusSpot) {
		updateSpecies(group.get(groupSpot),genus.get(genusSpot));
		return species;
	}

	public String getCommonName(int groupSpot, int genusSpot, int speciesSpot) {
		updateCommon(group.get(groupSpot), genus.get(genusSpot), species.get(speciesSpot));
		return commonName;
	}

	public void updateGroup() {

		InputStream inputStream = Resources.getSystem().openRawResource(
				R.raw.json);

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
		 * try { jsonreader.beginArray(); while (jsonreader.hasNext()){ //group
		 * array name jsonreader.beginObject(); groupName =
		 * jsonreader.nextString();
		 * 
		 * //start the array of the genus's in the group
		 * jsonreader.beginArray(); while(jsonreader.hasNext()) {
		 * jsonreader.beginObject(); genusName = jsonreader.nextString();
		 * //start collecting the species and common names from the genus array
		 * jsonreader.beginObject(); while(jsonreader.hasNext()) { //consumes
		 * the name of the json object name = jsonreader.nextName(); speciesName
		 * = jsonreader.nextString(); jsonreader.nextName(); commonName =
		 * jsonreader.nextString(); tempArray.add(speciesName + "," +
		 * commonName); Log.d("JSON",speciesName + " " + commonName); }
		 * jsonreader.close(); } jsonreader.close(); } jsonreader.close();
		 * 
		 * } catch (IOException e) { e.printStackTrace(); }
		 */

	}

	public void updateGenus(String groupName) {

		InputStream inputStream = Resources.getSystem().openRawResource(
				R.raw.json);

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

	public void updateSpecies(String groupName, String genusName) {

		InputStream inputStream = Resources.getSystem().openRawResource(
				R.raw.json);

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

	public void updateCommon(String groupName, String genusName,
			String speciesName) {

		InputStream inputStream = Resources.getSystem().openRawResource(
				R.raw.json);

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
												name = jsonreader.nextString();
												if (commonName.contains(name) == false) {
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
