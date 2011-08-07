package com.mozilla.bugzilla_etl.model.bug;

import com.mozilla.bugzilla_etl.model.Family;
import com.mozilla.bugzilla_etl.model.Field;


public class BugFields {

    /** Fields that provide (meta) data about a bug. All are non-versioned. */
    public static enum Bug implements Field {
        ID("bug_id"),
        REPORTED_BY,
        CREATION_DATE;

        public String columnName;

        @Override
        public String columnName() { return columnName; }

        @Override
        public Family family() { return Family.BUG; }

        Bug(String name) { columnName = name; }
        Bug() { columnName = name().toLowerCase(); }
    }


    /**
     * Simple fields of bugs that can be changed on activity basis in a
     * "from"/"to" fashion. Where available in bugzilla, they come from the bug
     * state and from the "what changed" in the activities table.
     *
     * Computed fields are not part of the input from the online Bugzilla
     * database, but are generated by {@link Bug#updateFacetsAndMeasurements}
     * during the version rebuilding.
     */
    public static enum Facet implements Field {
        ASSIGNED_TO,
        CHANGES(true),
        COMPONENT,
        FLAGS,
        KEYWORDS,
        GROUPS,
        OPSYS,
        PLATFORM,
        MAJOR_STATUS(true),
        MAJOR_STATUS_LAST_CHANGED_DATE(true),
        MODIFIED_FIELDS(true),
        PREVIOUS_MAJOR_STATUS(true),
        PREVIOUS_STATUS(true),
        PRIORITY,
        PRODUCT,
        RESOLUTION,
        SEVERITY,
        STATUS,
        STATUS_LAST_CHANGED_DATE(true),
        STATUS_WHITEBOARD,
        STATUS_WHITEBOARD_ITEMS(true),
        TARGET_MILESTONE,
        VERSION;

        public String columnName;

        @Override
        public String columnName() { return columnName; }

        @Override
        public Family family() { return Family.BUG_FACET; }

        public final boolean isComputed;

        /**
         * If initialized with a column name of <tt>null</tt>, this facet is
         * computed and not present in activities input.
         * @param columnName
         */
        Facet(boolean isComputed) {
            this.isComputed = isComputed;
            this.columnName = name().toLowerCase();
        }

        Facet() {
            this(false);
        }
    }


    /**
     * Measurements (facts) are computed after all versions of a bug have been
     * constructed. Day counts are measured at the end (valid to) of the
     * individual versions.
     *
     * When a measurement is not meaningful for a bug version, it has the value
     * -1 by convention. (:TODO: there is no support for NULL in LilyCMS, maybe
     * we could introduce it to SOLR using a custom formatter though?)
     *
     * All measurements are computed by {@link Bug#updateFacetsAndMeasurements}
     */
    public static enum Measurement implements Field {

        /**
         * How long has the bug been OPEN/CLOSED, also counting the current
         * version? Useful to get an impression on how long the "current" bugs
         * have been open.
         *
         * For the latest version of a bug, this field is -1 to indicate that it
         * is not available (yet). All we know is that the true value will be
         * *at least* NOW-modification_date.
         *
         * So whenever you get a min of -1 in a solr stats query, you might want
         * to exclude the current bug state (expiration_date:[* TO NOW]).
         */
        DAYS_IN_MAJOR_STATUS,

        /**
         * How long has the bug been in the current status, also counting the
         * current version? This -1 for the latest bug version (see above).
         */
        DAYS_IN_STATUS,

        /**
         * How long has the bug been OPENED before being CLOSED or CLOSED before
         * being (re-)OPENed. Use it to obtain the (average) time bugs stay
         * OPENED before being closed.
         *
         * This is -1 until there actually is a status change.
         */
        DAYS_IN_PREVIOUS_MAJOR_STATUS,

        /**
         * How long has the been in the previous status? This is <tt>null</tt>
         * for the first version and for versions where the status did not
         * change. Use it to obtain metrics such as the average time bugs stay
         * UNCONFIRMED.
         *
         * This is -1 for the first version of a bug.
         */
        DAYS_IN_PREVIOUS_STATUS,

        /**
         * How long has a bug been OPEN in total (including this version). This
         * is -1 for the latest version (see above).
         */
        DAYS_OPEN_ACCUMULATED,

        /**
         * How often has the bug been reopened (so far)? Defined for all
         * versions.
         */
        TIMES_REOPENED,

        /**
         * A version number from 1 to n (the latest version). Primarily useful
         * to select the first version.
         */
        NUMBER;

        public String columnName;

        @Override
        public String columnName() { return columnName; }

        @Override
        public Family family() { return Family.BUG_MEASURE; }

        Measurement(String name) { columnName = name; }
        Measurement() { columnName = name().toLowerCase(); }
    }

}
