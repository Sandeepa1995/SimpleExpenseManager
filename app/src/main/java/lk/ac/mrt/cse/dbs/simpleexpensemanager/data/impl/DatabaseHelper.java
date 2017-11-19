package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Damitha on 11/19/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String TABLE_NAME = "account";
    private static final String COL1 = "account_no";
    private static final String COL2 = "bank_name";
    private static final String COL3 = "account_holder_name";
    private static final String COL4 = "balance";

    private static final String TRTABLE_NAME = "transact";
    private static final String TRCOL1 = "date";
    private static final String TRCOL2 = "account_no";
    private static final String TRCOL3 = "expense_type";
    private static final String TRCOL4 = "amount";

    public DatabaseHelper(Context context){
        super(context,"150359E",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("+COL1+" TEXT PRIMARY KEY, " +
                COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " REAL)";
        db.execSQL(createTable);

        String createTableTR = "CREATE TABLE " + TRTABLE_NAME + " ("+TRCOL1+" TEXT, " +
                TRCOL2 + " TEXT, " + TRCOL3 + " TEXT, " + TRCOL4 + " REAL, PRIMARY KEY ("+ TRCOL1+","+TRCOL2+"), FOREIGN KEY ("+TRCOL2+") REFERENCES account ("+TRCOL2+"))";
        db.execSQL(createTableTR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int i, int i1){
        db.execSQL("DROP IF TABLE EXISTS "+ TABLE_NAME);
        db.execSQL("DROP IF TABLE EXISTS "+ TRTABLE_NAME);
        onCreate(db);
    }
}
