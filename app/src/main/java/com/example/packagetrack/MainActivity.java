package com.example.packagetrack;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    protected Client mKinveyClient;
    ExpandableListAdapter listAdapter;
    ExpandableListView listView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChildren;
    List<PackageEntity> entities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ExpandableListView) findViewById(R.id.expanded_menu);
        mKinveyClient = new Client.Builder("kid_SJEyn--we", "cac42e4156ce423a9cd9f083ac6f6dd1", this).build();
        if (!mKinveyClient.user().isUserLoggedIn()) {
            mKinveyClient.user().login("james", "Coffee99", new KinveyUserCallback() {
                @Override
                public void onSuccess(User user) {
                    System.out.println("Successful Login!");
                }

                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println("Failed Login");
                }
            });
        }
        final String company = (String)(mKinveyClient.user().get("company"));
        System.out.println(company);
        AsyncAppData<PackageEntity> mypackages = mKinveyClient.appData("containers", PackageEntity.class);

        mypackages.get(new KinveyListCallback<PackageEntity>() {
            @Override
            public void onSuccess(PackageEntity[] packageEntities) {
                System.out.println("Success!");
                listDataHeader = new ArrayList<String>(); //Entries/Entities/Containers
                listDataChildren = new HashMap<String, List<String>>(); //Children/Data
                for (PackageEntity e: packageEntities) {
                        if (e.status.equals("ready")){
                        String title = e.company;
                        List<String> otherInfo = new ArrayList<String>();
                        otherInfo.add("Contents: " + e.contents);
                        otherInfo.add("Quantity: " + new Integer(e.quantity).toString());
                        e.setStatus("ready"); //for testing, comment out later
                        otherInfo.add("Status: " + capitalizeWord(e.status));
                        listDataHeader.add(title);
                        listDataChildren.put(title, otherInfo);
                        entities.add(e);
                    }
                }
                listAdapter = new ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChildren);
                listView.setAdapter(listAdapter);

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                        final int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                        final List<String> otherInfo = listDataChildren.get(listAdapter.getGroup(groupPosition));
                        final PackageEntity e = entities.get(groupPosition);
                        System.out.println(e.status);

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setTitle("Confirm");
                            builder1.setMessage("Are You Sure You Want To Update Item " + listAdapter.getGroup(groupPosition) + " to Picked Up?");
                            builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    e.setStatus("picked up");
                                    view.setBackgroundColor(0xFF00FF00);
                                    otherInfo.remove(2);
                                    otherInfo.add("Status: " + e.status);
                                    listDataChildren.put((String) listAdapter.getGroup(groupPosition), otherInfo);
                                    listAdapter = new ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChildren);
                                    listView.setAdapter(listAdapter);
                                    Toast.makeText(getApplicationContext(), "Status Updated!", Toast.LENGTH_LONG).show();
                                    AsyncAppData<PackageEntity> mypackages = mKinveyClient.appData("containers", PackageEntity.class);
                                    mypackages.save(e, new KinveyClientCallback<PackageEntity>() {
                                        @Override
                                        public void onSuccess(PackageEntity packageEntity) {
                                            System.out.println("Successfully Saved " + packageEntity.id);
                                        }

                                        @Override
                                        public void onFailure(Throwable throwable) {
                                            //System.out.println("Saving Error");
                                        }
                                    });
                                }
                            });
                            builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                }
                            });
                            AlertDialog alert1 = builder1.create();
                            alert1.show();

                        return true;
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("Failed");
            }
        });
    }
    private String capitalizeWord(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

}
