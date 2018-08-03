package com.org.android.popularmovies.db.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.org.android.popularmovies.db.model.GenericColumn;
import com.org.android.popularmovies.db.table.TablesCreate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SqlitedatabaseOpenHelper that creates and updates the database..
 */
public class SqliteDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    public static int DATABASE_VERSION = 43;
    private static final String DATABASE_NAME = "Db_MOVIES";

    private static final String PRIMARY_KEY_STATEMENT = " PRIMARY KEY ";
    private static final String LEFT_PARANTHESIS = "( \n";
    private static final String SPACE_CHARACTER = " ";
    private static final String CONFLICT_REPALCE_TEXT = ") ON CONFLICT IGNORE ";

    private static final String UNIQUE_KEY_STATEMENT = " UNIQUE";

    public static final String PRIMARY_KEY_AUTOINCREMENT_STATEMENT = " PRIMARY KEY AUTOINCREMENT ";

    public SqliteDatabaseOpenHelper(Context context, String name) {
        super(context, name, null, DATABASE_VERSION);
    }

    public SqliteDatabaseOpenHelper(Context context) {
        this(context, DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    private void createTable(SQLiteDatabase db) {
        Collection<String> scripts = createScripts();
        for (String script : scripts) {
            db.execSQL(script);
        }
    }

    private Collection<String> createScripts() {
        Collection<String> scripts = new ArrayList<>();

        for (TablesCreate table : TablesCreate.values()) {
            String sqlQuery = CREATE_TABLE + table.getName() + LEFT_PARANTHESIS;
            String primaryKeyQuery = getQueryScript(table);
            sqlQuery += primaryKeyQuery;
            sqlQuery += ")\n";
            scripts.add(sqlQuery);
        }
        return scripts;
    }

    private String getQueryScript(TablesCreate table) {
        String query = "";
        for (int k = 0; k < table.getColumns().length; k++) {
            GenericColumn column = table.getColumns()[k];
            query += column.getFieldName() + SPACE_CHARACTER + column.getDataType().getDataTypeName();
            if (column.isPrimaryKey() && column.isAutoIncremented()) {
                query += PRIMARY_KEY_AUTOINCREMENT_STATEMENT;
            } else if (column.isPrimaryKey()) {
                query += PRIMARY_KEY_STATEMENT;
            } else if (column.isUnique()) {
                query += UNIQUE_KEY_STATEMENT + column.getFieldName() + CONFLICT_REPALCE_TEXT;
            }
            query += (k == table.getColumns().length - 1 ? "" : ",") + "\n";
        }


        return query;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db);
        createTable(db);
        DATABASE_VERSION++;
    }

    private void dropTable(SQLiteDatabase db) {
        List<String> dropTableScripts = createDropScripts();
        for (String script : dropTableScripts) {
            db.execSQL(script);
        }
    }

    private List<String> createDropScripts() {
        TablesCreate[] tables = TablesCreate.values();
        List<String> dropScripts = new ArrayList<String>();
        for (int k = 0; k < tables.length; k++) {
            String dropScript = "";
            TablesCreate table = tables[k];
            dropScript += "DROP TABLE IF EXISTS " + table.getName() + ";";
            dropScripts.add(dropScript);
        }
        return dropScripts;
    }


}
