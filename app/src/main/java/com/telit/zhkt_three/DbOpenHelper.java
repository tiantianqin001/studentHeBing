package com.telit.zhkt_three;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.telit.zhkt_three.greendao.AppInfoDao;
import com.telit.zhkt_three.greendao.DaoMaster;
import com.telit.zhkt_three.greendao.DiscussBeanDao;
import com.telit.zhkt_three.greendao.FillBlankBeanDao;
import com.telit.zhkt_three.greendao.LineMatchBeanDao;
import com.telit.zhkt_three.greendao.LocalResourceRecordDao;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;
import com.telit.zhkt_three.greendao.MulitBeanDao;
import com.telit.zhkt_three.greendao.SingleBeanDao;
import com.telit.zhkt_three.greendao.StudentInfoDao;
import com.telit.zhkt_three.greendao.SubjeatSaveBeanDao;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;

/**
 * author: qzx
 * Date: 2020/4/2 10:03
 */
public class DbOpenHelper extends DaoMaster.DevOpenHelper {
    public DbOpenHelper(Context context, String name) {
        super(context, name);
    }

    public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //切记不要调用super.onUpgrade(db,oldVersion,newVersion)
        if (oldVersion < newVersion) {
            MigrationHelper.migrate(db, AppInfoDao.class, DiscussBeanDao.class,
                    StudentInfoDao.class, LocalResourceRecordDao.class,
                    LocalTextAnswersBeanDao.class, LineMatchBeanDao.class,
                    SingleBeanDao.class, MulitBeanDao.class,
                    FillBlankBeanDao.class, SubjeatSaveBeanDao.class);
        }
    }
}
