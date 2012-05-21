/*
 * Copyright (C) 2012 VillainROM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may only use this file in compliance with the license and provided you are not associated with or are in co-operation anyone by the name 'X Vanderpoel'.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package jieehd.villain.updater;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

public class getfiles extends ListActivity {
	
 

private List<String> fileList = new ArrayList<String>();
private List<String> pathList = new ArrayList<String>();

private void ListDir(File f){
    File[] files = f.listFiles();
    fileList.clear();
    pathList.clear();
    for (File file : files){
     fileList.add(file.getPath().substring(28));
     pathList.add(file.getPath());
     final ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, R.layout.row, R.id.filename, fileList);
     setListAdapter(directoryList);
    }
};


   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       final ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, R.layout.row, R.id.filename, fileList);
       setListAdapter(directoryList);
       final String PATH = "/VillainROM/ROMs";
       final File SDDIR = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + PATH);
       ListDir(SDDIR);
       this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    	   public boolean onItemLongClick(AdapterView<?> parent, View view, final int pos, long id) {
    	       AlertDialog.Builder builder = new AlertDialog.Builder(getfiles.this);
    	       builder.setTitle("Options");
    	       builder.setItems(new String []{"Delete", "Re-name", "Install"}, new DialogInterface.OnClickListener() {



				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					   dialog.dismiss();
					   String Path = pathList.get(pos);
	    	           switch (which) {
	    	           case 0:
	    	        	  final  File file = new File(Path);
	    	        	   boolean deleted = file.delete();
	    	        	   setListAdapter(directoryList);
	        	           ListDir(SDDIR);
	        	           directoryList.notifyDataSetChanged();
	        	           if (deleted == true) {
	        	        	   Toast.makeText(getApplicationContext(), "File deleted succesfully", Toast.LENGTH_SHORT);
	        	           }else{
	        	        	   Toast.makeText(getApplicationContext(), "There was a problem while deleting the selected file", Toast.LENGTH_SHORT);
	        	           }
	    	        	   break;
	    	           case 1:
	    	        	    AlertDialog.Builder alert = new AlertDialog.Builder(getfiles.this);                 
	    	        	    alert.setTitle("Rename File");  
	    	        	    alert.setMessage("Enter new file name");                

	    	        	     // Set an EditText view to get user input   
	    	        	     final EditText input = new EditText(getfiles.this); 
	    	        	     alert.setView(input);

	    	        	        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
	    	        	        public void onClick(DialogInterface dialog, int whichButton) {  
	    	        	            String value = input.getText().toString();
	    	        	            String Name = fileList.get(pos);
	    	        	            File SDPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + PATH);
	    	        	            File from = new File(SDPath, Name);
	    	        	            File to = new File(SDPath, value + ".zip");
	    	        	            from.renameTo(to);
	    		    	        	setListAdapter(directoryList);
	    		        	        ListDir(SDPath);
	    		        	        directoryList.notifyDataSetChanged();
	    	        	            return; 
	    	        	           }  
	    	        	         });  

	    	        	        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

	    	        	            public void onClick(DialogInterface dialog, int which) {
	    	        	                // TODO Auto-generated method stub
	    	        	                return;   
	    	        	            }
	    	        	        });
	    	        	        alert.show();
	    	        	        break;
	    	        	   
	    	           case 2:
	    	        	    AlertDialog.Builder noworky = new AlertDialog.Builder(getfiles.this);                 
	    	        	    noworky.setTitle("O NOES!");  
	    	        	    noworky.setMessage("Unfortunately, due to limitations with the CWM recovery series, the only app that can install ROMs is ROM Manager. Sorry.");    
    	        	        noworky.setNegativeButton("Ok :(", new DialogInterface.OnClickListener() {

    	        	            public void onClick(DialogInterface dialog, int which) {
    	        	                // TODO Auto-generated method stub
    	        	                return;   
    	        	            }
    	        	        });
    	        	        noworky.show();
	    	           	break;
	    	        	   
	    	           }
				}
    	    	 
    	    	});
    	       	           
    	   
    	    
    	        builder.setCancelable(true);
    	        builder.create().show();
    	        return true;
    	   }
       });
       
       
   }
   

 
}