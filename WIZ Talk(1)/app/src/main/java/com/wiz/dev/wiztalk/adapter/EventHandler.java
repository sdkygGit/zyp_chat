/*
    Open Manager, an open source file manager for the Android system
    Copyright (C) 2009, 2010, 2011  Joe Berria <nexesdevelopment@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.wiz.dev.wiztalk.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.utils.FileManager;

/**
 * This class sits between the Main activity and the FileManager class. To keep
 * the FileManager class modular, this class exists to handle UI events and
 * communicate that information to the FileManger class
 * 
 * This class is responsible for the buttons onClick method. If one needs to
 * change the functionality of the buttons found from the Main activity or add
 * button logic, this is the class that will need to be edited.
 * 
 * This class is responsible for handling the information that is displayed from
 * the list view (the files and folder) with a a nested class TableRow. The
 * TableRow class is responsible for displaying which icon is shown for each
 * entry. For example a folder will display the folder icon, a Word doc will
 * display a word icon and so on. If more icons are to be added, the TableRow
 * class must be updated to display those changes.
 * 
 * @author Joe Berria
 */
public class EventHandler {
	/*
	 * Unique types to control which file operation gets performed in the
	 * background
	 */
	private static final int SEARCH_TYPE = 0x00;
	private static final int COPY_TYPE = 0x01;
	private static final int UNZIP_TYPE = 0x02;
	private static final int UNZIPTO_TYPE = 0x03;
	private static final int ZIP_TYPE = 0x04;
	private static final int DELETE_TYPE = 0x05;
	private static final int MANAGE_DIALOG = 0x06;

	private final Context mContext;
	private final FileManager mFileMang;
	private TableRow mDelegate;

	private boolean multi_select_flag = false;
	private boolean delete_after_copy = false;
	private int mColor = Color.WHITE;

	// the list used to feed info into the array adapter and when multi-select
	// is on
	private ArrayList<String> mDataSource;
	private TextView mPathLabel;

	/**
	 * Creates an EventHandler object. This object is used to communicate most
	 * work from the Main activity to the FileManager class.
	 * 
	 * @param context
	 *            The context of the main activity e.g Main
	 * @param manager
	 *            The FileManager object that was instantiated from Main
	 */
	public EventHandler(String path, Context context, final FileManager manager) {
		mContext = context;
		mFileMang = manager;

		mDataSource = new ArrayList<String>(mFileMang.setHomeDir(path));
	}

	/**
	 * This constructor is called if the user has changed the screen orientation
	 * and does not want the directory to be reset to home.
	 * 
	 * @param context
	 *            The context of the main activity e.g Main
	 * @param manager
	 *            The FileManager object that was instantiated from Main
	 * @param location
	 *            The first directory to display to the user
	 */
	public EventHandler(Context context, final FileManager manager, String location) {
		mContext = context;
		mFileMang = manager;

		mDataSource = new ArrayList<String>(mFileMang.getNextDir(location, true));
	}

	/**
	 * This method is called from the Main activity and this has the same
	 * reference to the same object so when changes are made here or there they
	 * will display in the same way.
	 * 
	 * @param adapter
	 *            The TableRow object
	 */
	public void setListAdapter(TableRow adapter) {
		mDelegate = adapter;
	}

	/**
	 * This method is called from the Main activity and is passed the TextView
	 * that should be updated as the directory changes so the user knows which
	 * folder they are in.
	 * 
	 * @param path
	 *            The label to update as the directory changes
	 *            the label to update information
	 */
	public void setUpdateLabels(TextView path) {
		mPathLabel = path;

		upDataPath();
	}

	/**
	 * 
	 * @param color
	 */
	public void setTextColor(int color) {
		mColor = color;
	}

	/**
	 * If you want to move a file (cut/paste) and not just copy/paste use this
	 * method to tell the file manager to delete the old reference of the file.
	 * 
	 * @param delete
	 *            true if you want to move a file, false to copy the file
	 */
	public void setDeleteAfterCopy(boolean delete) {
		delete_after_copy = delete;
	}

	/**
	 * Indicates whether the user wants to select multiple files or folders at a
	 * time. <br>
	 * <br>
	 * false by default
	 * 
	 * @return true if the user has turned on multi selection
	 */
	public boolean isMultiSelected() {
		return multi_select_flag;
	}

	/**
	 * This method, handles the button presses of the top buttons found in the
	 * Main activity.
	 */

	public boolean goBack() {
		return updateDirectory(mFileMang.getPreviousDir());
	}

	void upDataPath() {
		String path = mFileMang.getCurrentDir();
		String matchSdcard0 = Environment.getExternalStorageDirectory().getPath();
		String matchSdcard1 = "/storage/sdcard1";
		if (path.startsWith(matchSdcard0))
			path = path.replaceFirst(matchSdcard0, "/手机内存");
		else if (path.startsWith(matchSdcard1))
			path = path.replaceFirst(matchSdcard1, "/SD卡内存");
		mPathLabel.setText(path);

	}

	/**
	 * will return the data in the ArrayList that holds the dir contents.
	 * 
	 * @param position
	 *            the indext of the arraylist holding the dir content
	 * @return the data in the arraylist at position (position)
	 */
	public String getData(int position) {
		if (position > mDataSource.size() - 1 || position < 0)
			return null;
		return mDataSource.get(position);
	}

	/**
	 * called to update the file contents as the user navigates there phones
	 * file system.
	 * 
	 * @param content
	 *            an ArrayList of the file/folders in the current directory.
	 */
	public boolean updateDirectory(ArrayList<String> content) {
		if (content == null)
			return false;

		if (!mDataSource.isEmpty())
			mDataSource.clear();
		for (String data : content)
			mDataSource.add(data);
		mDelegate.notifyDataSetChanged();
		upDataPath();
		if (mDelegate != null) {
			mDelegate.clearPos();
		}
		return true;
	}

	/**
	 * This private method is used to display options the user can select when
	 * the tool box button is pressed. The WIFI option is commented out as it
	 * doesn't seem to fit with the overall idea of the application. However to
	 * display it, just uncomment the below code and the code in the
	 * AndroidManifest.xml file.
	 */

	private static class ViewHolder {
		TextView topView;
		TextView bottomView;
		ImageView icon;
		ImageView ivRight;
		CheckBox mSelect; // multi-select check mark icon
	}

	/**
	 * A nested class to handle displaying a custom view in the ListView that is
	 * used in the Main activity. If any icons are to be added, they must be
	 * implemented in the getView method. This class is instantiated once in
	 * Main and has no reason to be instantiated again.
	 * 
	 * @author Joe Berria
	 */
	public class TableRow extends ArrayAdapter<String> {
		private final int KB = 1024;
		private final int MG = KB * KB;
		private final int GB = MG * KB;
		private String display_size;
		private Map<Integer, File> positions;

		public void clearPos() {
			if (positions != null && positions.size() > 0)
				positions.clear();
		}

		public TableRow() {
			super(mContext, R.layout.memery_item_row, mDataSource);
		}

		@SuppressLint("UseSparseArrays")
		public boolean  addMultiPosition(int index, File file) {
			if (positions == null)
				positions = new HashMap<Integer, File>();
			if(positions.containsKey(index)){
				positions.remove(index);
				notifyDataSetChanged();
			}else{
				if (positions.size() > 0) {
					Toast.makeText(mContext, "只能选择1个文件", Toast.LENGTH_SHORT).show();
					return false;
				} else {
					positions.put(index, file);
					notifyDataSetChanged();
				}
			}
			return true;
		}

		public Set<Entry<Integer, File>> getSelectFile() {
			if (positions != null && positions.size() > 0) {
				return positions.entrySet();
			}
			return null;
		}

		/**
		 * This will turn off multi-select and hide the multi-select buttons at
		 * the bottom of the view.
		 * 
		 * @param clearData
		 *            if this is true any files/folders the user selected for
		 *            multi-select will be cleared. If false, the data will be
		 *            kept for later use. Note: multi-select copy and move will
		 *            usually be the only one to pass false, so we can later
		 *            paste it to another folder.
		 */

		public String getFilePermissions(File file) {
			String per = "-";

			if (file.isDirectory())
				per += "d";
			if (file.canRead())
				per += "r";
			if (file.canWrite())
				per += "w";

			return per;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder mViewHolder;
			int num_items = 0;
			String temp = mFileMang.getCurrentDir();
			final File file = new File(temp + "/" + mDataSource.get(position));
			String[] list = file.list();

			if (list != null)
				num_items = list.length;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.memery_item_row, parent, false);
				mViewHolder = new ViewHolder();
				mViewHolder.topView = (TextView) convertView.findViewById(R.id.top_view);
				mViewHolder.bottomView = (TextView) convertView.findViewById(R.id.bottom_view);
				mViewHolder.icon = (ImageView) convertView.findViewById(R.id.row_image);
				mViewHolder.ivRight = (ImageView) convertView.findViewById(R.id.ivRight);
				mViewHolder.mSelect = (CheckBox) convertView.findViewById(R.id.checkBox);
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}

			if (file != null && file.isFile()) {
				mViewHolder.mSelect.setVisibility(View.VISIBLE);
				mViewHolder.ivRight.setVisibility(View.GONE);
				mViewHolder.mSelect.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(!addMultiPosition(position, file)){
							((CheckBox)v).setChecked(false);
						}
					}
				});
				
				if (positions != null && positions.containsKey(position))
					mViewHolder.mSelect.setChecked(true);
				else
					mViewHolder.mSelect.setChecked(false);

				String ext = file.toString();
				String sub_ext = ext.substring(ext.lastIndexOf(".") + 1);

				/*
				 * This series of else if statements will determine which icon
				 * is displayed
				 */
				if (sub_ext.equalsIgnoreCase("pdf")) {
					mViewHolder.icon.setImageResource(R.drawable.pdf);

				} else if (sub_ext.equalsIgnoreCase("mp3") || sub_ext.equalsIgnoreCase("wma")
						|| sub_ext.equalsIgnoreCase("m4a") || sub_ext.equalsIgnoreCase("m4p")
						|| sub_ext.equalsIgnoreCase("amr")) {
					mViewHolder.icon.setImageResource(R.drawable.ic_select_file_music);
				} else if (sub_ext.equalsIgnoreCase("png") || sub_ext.equalsIgnoreCase("jpg")
						|| sub_ext.equalsIgnoreCase("jpeg") || sub_ext.equalsIgnoreCase("gif")
						|| sub_ext.equalsIgnoreCase("tiff")) {
					mViewHolder.icon.setImageResource(R.drawable.image);
				} else if (sub_ext.equalsIgnoreCase("zip") || sub_ext.equalsIgnoreCase("gzip")
						|| sub_ext.equalsIgnoreCase("gz")) {
					mViewHolder.icon.setImageResource(R.drawable.zip);
				} else if (sub_ext.equalsIgnoreCase("m4v") || sub_ext.equalsIgnoreCase("wmv")
						|| sub_ext.equalsIgnoreCase("3gp") || sub_ext.equalsIgnoreCase("mp4")) {
					mViewHolder.icon.setImageResource(R.drawable.movies);
				} else if (sub_ext.equalsIgnoreCase("doc") || sub_ext.equalsIgnoreCase("docx")) {
					mViewHolder.icon.setImageResource(R.drawable.word);
				} else if (sub_ext.equalsIgnoreCase("xls") || sub_ext.equalsIgnoreCase("xlsx")) {
					mViewHolder.icon.setImageResource(R.drawable.excel);
				} else if (sub_ext.equalsIgnoreCase("ppt") || sub_ext.equalsIgnoreCase("pptx")) {
					mViewHolder.icon.setImageResource(R.drawable.ppt);
				} else if (sub_ext.equalsIgnoreCase("html")) {
					mViewHolder.icon.setImageResource(R.drawable.html32);
				} else if (sub_ext.equalsIgnoreCase("xml")) {
					mViewHolder.icon.setImageResource(R.drawable.xml32);
				} else if (sub_ext.equalsIgnoreCase("conf")) {
					mViewHolder.icon.setImageResource(R.drawable.config32);
				} else if (sub_ext.equalsIgnoreCase("apk")) {
					mViewHolder.icon.setImageResource(R.drawable.appicon);
				} else if (sub_ext.equalsIgnoreCase("jar")) {
					mViewHolder.icon.setImageResource(R.drawable.jar32);
				} else {
					mViewHolder.icon.setImageResource(R.drawable.text);
				}

			} else if (file != null && file.isDirectory()) {
				if (file.canRead() && file.list().length > 0)
					mViewHolder.icon.setImageResource(R.drawable.folder_full);
				else
					mViewHolder.icon.setImageResource(R.drawable.folder_full);
//					mViewHolder.icon.setImageResource(R.drawable.folder);
				mViewHolder.ivRight.setVisibility(View.VISIBLE);
				mViewHolder.mSelect.setVisibility(View.GONE);
			}

//			String permission = getFilePermissions(file);

			if (file.isFile()) {
				double size = file.length();
				if (size > GB)
					display_size = String.format("%.2f Gb ", (double) size / GB);
				else if (size < GB && size > MG)
					display_size = String.format("%.2f Mb ", (double) size / MG);
				else if (size < MG && size > KB)
					display_size = String.format("%.2f Kb ", (double) size / KB);
				else
					display_size = String.format("%.2f bytes ", (double) size);

				if (file.isHidden())
					mViewHolder.bottomView.setText("(hidden) | " + display_size + "  "
					// + permission
					);
				else
					mViewHolder.bottomView.setText(display_size + "  "
					// + permission
					);

			} else {
				if (file.isHidden())
					mViewHolder.bottomView.setText("(hidden) | " + num_items + " 项  "
					// + permission
					);
				else
					mViewHolder.bottomView.setText(num_items + " 项  "
					// + permission
					);
			}

			mViewHolder.topView.setText(file.getName());

			return convertView;
		}

	}

}
