/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Debug;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType.EXPENSE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType.INCOME;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
//    private final List<Transaction> transactions;
    private static final String TAG="PersistentTransactionDAO";

    private static final String TABLE_NAME = "transact_150359E";
    private static final String COL1 = "date";
    private static final String COL2 = "account_no";
    private static final String COL3 = "expense_type";
    private static final String COL4 = "amount";

    public PersistentTransactionDAO(Context context) {
        super(context,TABLE_NAME,null,1);
//        transactions = new LinkedList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("+COL1+" TEXT, " +
                COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " REAL, PRIMARY KEY ("+ COL1+","+COL2+"), FOREIGN KEY ("+COL2+") REFERENCES account ("+COL2+"))";
//        String createTable = "CREATE TABLE " + TABLE_NAME + " ("+COL1+" TEXT PRIMARY KEY, " +
//                COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int i, int i1){
        db.execSQL("DROP IF TABLE EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues conval = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Or whatever format fits best your needs.
        String dateStr = sdf.format(date);

        conval.put(COL1, dateStr);
        conval.put(COL2, accountNo);
        conval.put(COL3, expenseType.name());
        conval.put(COL4, amount);

        long result = db.insert(TABLE_NAME,null,conval);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db=this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query,null);
        ArrayList<Transaction> dataList = new ArrayList<Transaction>();
        for (data.moveToFirst();!data.isAfterLast();data.moveToNext()){
            try {
                ExpenseType expenseType;
                if (data.getString(data.getColumnIndex(COL3))=="EXPENSE"){
                    expenseType = EXPENSE;
                }
                else{
                    expenseType = INCOME;
                }
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dateDte = sdf.parse(data.getString(data.getColumnIndex(COL1)));
                Transaction transaction = new Transaction(dateDte,
                        data.getString(data.getColumnIndex(COL2)),
                        expenseType,
                        data.getDouble(data.getColumnIndex(COL4)));
                dataList.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return dataList;
//        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db=this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query,null);
        ArrayList<Transaction> dataList = new ArrayList<Transaction>();
        for (data.moveToFirst();!data.isAfterLast();data.moveToNext()){
            try {
                ExpenseType expenseType;
                if (data.getString(data.getColumnIndex(COL3))=="EXPENSE"){
                    expenseType = EXPENSE;
                }
                else{
                    expenseType = INCOME;
                }
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dateDte = sdf.parse(data.getString(data.getColumnIndex(COL1)));
                Transaction transaction = new Transaction(dateDte,
                        data.getString(data.getColumnIndex(COL2)),
                        expenseType,
                        data.getDouble(data.getColumnIndex(COL4)));
                dataList.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int size = data.getCount();
        if (size <= limit) {
            return dataList;
        }
        // return the last <code>limit</code> number of transaction logs
        return dataList.subList(size - limit, size);
    }

}
