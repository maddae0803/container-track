package com.example.packagetrack;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.kinvey.java.model.KinveyMetaData;

import java.util.ArrayList;

/**
 * Created by maxwelladdae0803 on 1/21/17.
 */

public class PackageEntity extends GenericJson {

    @Key
    private int number;
    @Key("_id")
    protected String id;
    @Key("_kmd")
    private KinveyMetaData meta; // Kinvey metadata, OPTIONAL
    @Key("_acl")
    private KinveyMetaData.AccessControlList acl; //Kinvey access control, OPTIONAL
    @Key
    protected String company;
    @Key
    protected String contents;
    @Key
    protected int quantity;
    @Key
    protected String status;

    public PackageEntity() {}

    public PackageEntity(int n, String i, KinveyMetaData m, KinveyMetaData.AccessControlList a, String com, String con, int q, String s) {
        number = n;
        id = i;
        meta = m;
        acl = a;
        company = com;
        contents = con;
        quantity = q;
        status = s;

    }
    public void setStatus(String s) {
        status = s;
    }
    public ArrayList<String> getImportantStuff() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(id);
        ret.add(company);
        ret.add(contents);
        ret.add(status);
        return ret;
    }

}
