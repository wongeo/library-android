package com.feng.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

/**
 * 数据库层实现
 * 
 * @author WangJing
 * 
 * @param <T>
 */
public class DatabaseHelper<T extends Object> extends SQLiteOpenHelper {

	/** 数据实体类型 */
	private Class<T> mClazz;

	/** 表注解对象 */
	private Table mTable;

	/** 数据库更新监听 */
	private AlterTableCallback mAlterTableCallback;

	/**
	 * 数据库构造函数
	 * 
	 * @param context
	 * @param dir
	 * @param clazz
	 * @param callback
	 */
	public DatabaseHelper(Context context, String dir, Class<T> clazz, AlterTableCallback callback) {
		super(context, dir + "/" + clazz.getAnnotation(Table.class).name(), null, clazz.getAnnotation(Table.class).version());
		mClazz = clazz;
		mTable = mClazz.getAnnotation(Table.class);
		mAlterTableCallback = callback;
	}

	/**
	 * 数据库构造函数
	 * 
	 * @param context
	 * @param clazz
	 * @param callback
	 */
	public DatabaseHelper(Context context, Class<T> clazz, AlterTableCallback callback) {
		super(context, clazz.getAnnotation(Table.class).name(), null, clazz.getAnnotation(Table.class).version());
		mClazz = clazz;
		mTable = mClazz.getAnnotation(Table.class);
		mAlterTableCallback = callback;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = createTableSql();
		db.execSQL(sql);

		List<String> sqls = createIndexSQL();
		for (String indexSql : sqls) {
			db.execSQL(indexSql);
		}
	}

	/**
	 * 创建数据库表
	 * 
	 * @return
	 */
	private String createTableSql() {
		String sql = "create table if not exists " + mTable.name() + "(";
		Field[] fields = mClazz.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			Column column = fields[i].getAnnotation(Column.class);
			if (column == null) {
				continue;
			}
			sql += column.name();
			if (column.type() == DataType.INT) {
				sql += " integer";
			} else if (column.type() == DataType.LONG) {
				sql += " long";
			} else if (column.type() == DataType.TEXT) {
				sql += " text";
			}

			if (column.key()) {
				sql += " primary key autoincrement";
			}
			if (!TextUtils.isEmpty(column.dvalue())) {
				sql += " default " + column.dvalue();
			}
			sql += ",";
		}
		sql = sql.substring(0, sql.length() - 1);

		sql += ")";
		return sql;
	}

	private List<String> createIndexSQL() {
		List<String> sqls = new ArrayList<String>();
		Field[] fields = mClazz.getDeclaredFields();
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			if (column == null || column.key()) {
				continue;
			}
			String sql = "CREATE INDEX " + column.name() + "_index ON " + mTable.name() + " (" + column.name() + ")";
			sqls.add(sql);
		}
		return sqls;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (mAlterTableCallback == null) {
			return;
		}
		List<String> sqls = mAlterTableCallback.onUpgrade(oldVersion, newVersion);
		for (String sql : sqls) {
			db.execSQL(sql);
		}
	}

	/**
	 * 获取全部内容
	 * 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public List<T> query() throws InstantiationException, IllegalAccessException {
		String sql = "select * from " + mTable.name();
		return query(sql);
	}

	/**
	 * 查询按照指定属性值
	 * 
	 * @param t
	 * @param fields
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 */
	public List<T> query(T t, Field... fields) throws IllegalAccessException, IllegalArgumentException, InstantiationException {
		String where = " where 1=1";
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			if (column.type() == DataType.INT) {
				where += " and " + column.name() + "=" + field.getInt(t);
			} else if (column.type() == DataType.LONG) {
				where += " and " + column.name() + "=" + field.getLong(t);
			} else if (column.type() == DataType.TEXT) {
				where += " and " + column.name() + "=" + "'" + field.get(t) + "'";
			}
		}
		String sql = "select * from " + mTable.name() + where;
		return query(sql);
	}

	/**
	 * 用SQL语句进行查询
	 * 
	 * @param sql
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public List<T> query(String sql) throws InstantiationException, IllegalAccessException {
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		List<T> result = new ArrayList<T>();
		while (cursor.moveToNext()) {
			T dataItem = parseFromCursor(cursor);
			result.add(dataItem);
		}
		return result;
	}

	/**
	 * 删除
	 * 
	 * @param id
	 */
	public void delete(int id) {
		SQLiteDatabase db = getWritableDatabase();
		String[] whereArgs = { id + "" };
		db.delete(mTable.name(), "_id=?", whereArgs);
	}

	/**
	 * 插入一条数据
	 * 
	 * @param t
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public void insert(T t) throws IllegalAccessException, IllegalArgumentException {
		ContentValues values = parseXtoContentValues(t);
		SQLiteDatabase db = getWritableDatabase();
		db.insert(mTable.name(), null, values);
	}

	/**
	 * 更新数据库
	 * 
	 * @param t
	 * @param id
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public void update(T t, int id) throws IllegalAccessException, IllegalArgumentException {
		ContentValues values = parseXtoContentValues(t);
		SQLiteDatabase db = getWritableDatabase();
		String[] whereArgs = { id + "" };
		db.update(mTable.name(), values, "_id=?", whereArgs);
	}

	/**
	 * 数据格式转化
	 * 
	 * @param cursor
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private T parseFromCursor(Cursor cursor) throws InstantiationException, IllegalAccessException {
		T t = mClazz.newInstance();
		Field[] fields = mClazz.getDeclaredFields();
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			if (column == null) {
				continue;
			}
			if (column.type() == DataType.INT) {
				int value = cursor.getInt(cursor.getColumnIndex(column.name()));
				field.setInt(t, value);
			} else if (column.type() == DataType.LONG) {
				long value = cursor.getLong((cursor.getColumnIndex(column.name())));
				field.setLong(t, value);
			} else if (column.type() == DataType.TEXT) {
				String value = cursor.getString(cursor.getColumnIndex(column.name()));
				field.set(t, value);
			}
		}
		return t;
	}

	/**
	 * 将X换成ContentValues
	 * 
	 * @param t
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private ContentValues parseXtoContentValues(T t) throws IllegalAccessException, IllegalArgumentException {
		Field[] fields = mClazz.getFields();
		ContentValues values = new ContentValues();
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			if (column == null || column.key()) {
				continue;
			}
			if (column.type() == DataType.INT) {
				values.put(column.name(), field.getInt(t));
			} else if (column.type() == DataType.LONG) {
				values.put(column.name(), field.getLong(t));
			} else if (column.type() == DataType.TEXT) {
				values.put(column.name(), (String) field.get(t));
			}
		}
		return values;
	}

	/**
	 * 数据库更新回调
	 * 
	 * @author WangJing
	 * 
	 */
	public interface AlterTableCallback {
		List<String> onUpgrade(int oldVersion, int newVersion);
	}
}
