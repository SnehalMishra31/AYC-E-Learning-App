package com.alphaCoachingAdmin.LocalDB;

import android.provider.BaseColumns;

public class DbTables {

    public static final class StandardsTable implements BaseColumns {
        public static final String TABLE_NAME = "standard";
        public static final String ID = "categoryId";
        public static final String STANDARD = "standard";
    }

    public static final class SubjectTable implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String ID = "subject_id";
        public static final String SUBJECT_NAME = "subject_name";
        public static final String STANDARD_ID = "standard_id";
    }
}
