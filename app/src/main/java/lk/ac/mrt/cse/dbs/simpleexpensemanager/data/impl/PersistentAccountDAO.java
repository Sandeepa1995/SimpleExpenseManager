package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Damitha on 11/17/2017.
 */

public class PersistentAccountDAO implements AccountDAO {
    private static final String TAG="PersistentAccountDAO";

    private static final String TABLE_NAME = "account";
    private static final String COL1 = "account_no";
    private static final String COL2 = "bank_name";
    private static final String COL3 = "account_holder_name";
    private static final String COL4 = "balance";

    public static DatabaseHelper dbhelper;

    public PersistentAccountDAO(Context context) {
        if (PersistentTransactionDAO.dbhelper==null){
            dbhelper=new DatabaseHelper(context);
        }
        else {
            this.dbhelper=PersistentTransactionDAO.dbhelper;
        }
    }


    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db=this.dbhelper.getReadableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query,null);
        ArrayList<String> dataList = new ArrayList<String>();
        for (data.moveToFirst();!data.isAfterLast();data.moveToNext()){
            dataList.add(data.getString(data.getColumnIndex(COL1)));
        }
        return dataList;
//        return new ArrayList<>(accounts.keySet());
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db=this.dbhelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query,null);
        ArrayList<Account> dataList = new ArrayList<Account>();
        for (data.moveToFirst();!data.isAfterLast();data.moveToNext()){
            Account account = new Account(data.getString(data.getColumnIndex(COL1)),
                    data.getString(data.getColumnIndex(COL2)),
                    data.getString(data.getColumnIndex(COL3)),
                    data.getDouble(data.getColumnIndex(COL4)));
            dataList.add(account);
        }
        return dataList;
//        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db=this.dbhelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL1 +"=?";
        Cursor data = db.rawQuery(query,new String[] { accountNo });
        Account account=new Account("","","",0);
        for (data.moveToFirst();!data.isAfterLast();data.moveToNext()){
            account = new Account(data.getString(data.getColumnIndex(COL1)),
                    data.getString(data.getColumnIndex(COL1)),
                    data.getString(data.getColumnIndex(COL1)),
                    data.getDouble(data.getColumnIndex(COL1)));
        }
        if (account.getAccountNo()!="") {
            return account;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db=this.dbhelper.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL1 +"=?";
        Cursor data = db.rawQuery(query,new String[] { account.getAccountNo() });
        if (data.getCount()==0){
            ContentValues conval = new ContentValues();
            conval.put(COL1,account.getAccountNo());
            conval.put(COL2, account.getBankName());
            conval.put(COL3, account.getAccountHolderName());
            conval.put(COL4, account.getBalance());

            long result = db.insert(TABLE_NAME,null,conval);
        }
//        accounts.put(account.getAccountNo(), account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (getAccount(accountNo).getAccountNo()=="") {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        SQLiteDatabase db = this.dbhelper.getWritableDatabase();
        String queryE = "DELETE FROM " + TABLE_NAME +" WHERE " + COL1 +"=?";
        db.execSQL(queryE,new String[] { accountNo });
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account acc = getAccount(accountNo);
        if (acc.getAccountNo()=="") {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        SQLiteDatabase db = this.dbhelper.getWritableDatabase();
        switch (expenseType) {
            case EXPENSE:
                String queryE = "UPDATE " + TABLE_NAME + " SET "+ COL4 +" =? WHERE " + COL1 +"=?";
                db.execSQL(queryE,new String[] { Double.toString(acc.getBalance()-amount),accountNo });
                break;
            case INCOME:
                String queryI = "UPDATE " + TABLE_NAME + " SET "+ COL4 +" =? WHERE " + COL1 +"=?";
                db.execSQL(queryI,new String[] { Double.toString(acc.getBalance()+amount),accountNo });
                break;
        }
    }
}
