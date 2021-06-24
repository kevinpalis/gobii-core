package org.gobiiproject.gobiidao.resultset.sqlworkers.read.liststatement.paged;

import java.util.HashMap;
import java.util.Map;
import org.gobiiproject.gobiidao.resultset.core.listquery.ParameterizedSql;

/*
* This class is the core of paging sql. The page frame methods returns the sql for a table that
* specifies the pages and their boundaries, where "boundary" is defined as a compound unique
* value consisting of the name and the primary key. The single page method returns the sql for
* retrieving one page's worth of rows from the page frame. The idea is that the value in each row of
* the page frame define the last row of the previous page, such that the query gives you the PAGELIIMITth
* page's records.
* */
@SuppressWarnings({"serial"})
public class PagerSql {

    Map<String, Object> sqlParamVals;

    public PagerSql(Map<String, Object> sqlParamVals) {
        this.sqlParamVals = sqlParamVals;
    } //ctor

    public static final String PARAM_NAME_NAME_COL = "nameCol";
    public static final String PARAM_NAME_ID_COL = "idCol";
    public static final String PARAM_NAME_COL_TABLE_ALIAS = "colTableAlias";
    public static final String PARAM_NAME_SELECT_COLS = "selectClause";
    public static final String PARAM_NAME_FROM_CLAUSE = "fromClause";
    public static final String PARAM_NAME_PAGE_SIZE = "pageSize";
    public static final String PARAM_NAME_NAME_COL_VAL = "nameColVal";
    public static final String PARAM_NAME_ID_COL_VAL = "idColVal";
    public static final String PARAM_NAME_PAGE_NUMBER_COL_ALIAS = "pageNumberColAlias";


    // the SELECT_CLAUSE parameter value must include the columns identified as the NAME_COL and ID_COL
    private final String PARAM_NAME_NAME_COL_DELIMITED = ParameterizedSql.makeDelimitedParamName(PARAM_NAME_NAME_COL);
    private final String PARAM_NAME_COL_TABLE_ALIAS_DELIMITED = ParameterizedSql.makeDelimitedParamName(PARAM_NAME_COL_TABLE_ALIAS);
    private final String PARAM_NAME_ID_COL_DELIMITED = ParameterizedSql.makeDelimitedParamName(PARAM_NAME_ID_COL);
    private final String PARAM_NAME_SELECT_COLS_DELIMITED = ParameterizedSql.makeDelimitedParamName(PARAM_NAME_SELECT_COLS);
    private final String PARAM_NAME_FROM_CLAUSE_DELIMITED = ParameterizedSql.makeDelimitedParamName(PARAM_NAME_FROM_CLAUSE);
    private final String PARAM_NAME_PAGE_SIZE_DELIMITED = ParameterizedSql.makeDelimitedParamName(PARAM_NAME_PAGE_SIZE);
    private final String PARAM_NAME_NAME_COL_VAL_DELIMITED = ParameterizedSql.makeDelimitedParamName(PARAM_NAME_NAME_COL_VAL);
    private final String PARAM_NAME_ID_COL_VAL_DELIMITED = ParameterizedSql.makeDelimitedParamName(PARAM_NAME_ID_COL_VAL);
    private final String PARAM_NAME_PAGE_NUMBER_COL_ALIAS_DELIMITED = ParameterizedSql.makeDelimitedParamName(PARAM_NAME_PAGE_NUMBER_COL_ALIAS);
    ;

    public static String getPageNumberColName() {
        return "page_number";
    }


    public String makePageBoundarySql() {

        String pageBoundarySqlTemplate = "select " +
                " x." + PARAM_NAME_NAME_COL_DELIMITED + ", " +
                " x." + PARAM_NAME_ID_COL_DELIMITED + ", " +
                " row_number() over( " +
                " order by " +
                " x." + PARAM_NAME_NAME_COL_DELIMITED + ", " +
                " x." + PARAM_NAME_ID_COL_DELIMITED + " " +
                " ) + 1 " + PARAM_NAME_PAGE_NUMBER_COL_ALIAS_DELIMITED +
                " from " +
                " ( " +
                " select " +
                PARAM_NAME_COL_TABLE_ALIAS_DELIMITED + "." + PARAM_NAME_NAME_COL_DELIMITED + ", " +
                PARAM_NAME_COL_TABLE_ALIAS_DELIMITED + "." + PARAM_NAME_ID_COL_DELIMITED + ", " +
                " case " +
                " row_number() over( " +
                " order by " +
                PARAM_NAME_COL_TABLE_ALIAS_DELIMITED + "." + PARAM_NAME_NAME_COL_DELIMITED + "," +
                PARAM_NAME_COL_TABLE_ALIAS_DELIMITED + "." + PARAM_NAME_ID_COL_DELIMITED +
                " ) % " + PARAM_NAME_PAGE_SIZE_DELIMITED +
                " when 0 then 1 " +
                " else 0 " +
                " end page_boundary " +
                PARAM_NAME_FROM_CLAUSE_DELIMITED +
                " order by " +
                PARAM_NAME_COL_TABLE_ALIAS_DELIMITED + "." + PARAM_NAME_NAME_COL_DELIMITED + "," +
                PARAM_NAME_COL_TABLE_ALIAS_DELIMITED + "." + PARAM_NAME_ID_COL_DELIMITED +
                ") x " +
                " where " +
                " x.page_boundary = 1";

        ParameterizedSql parameterizedSql =
                new ParameterizedSql(pageBoundarySqlTemplate,
                        new HashMap<String, String>() {
                            {
                                put(PARAM_NAME_NAME_COL_DELIMITED, null);
                                put(PARAM_NAME_ID_COL_DELIMITED, null);
                                put(PARAM_NAME_COL_TABLE_ALIAS_DELIMITED, null);
                                put(PARAM_NAME_PAGE_SIZE_DELIMITED, null);
                                put(PARAM_NAME_FROM_CLAUSE_DELIMITED, null);
                                put(PARAM_NAME_PAGE_NUMBER_COL_ALIAS_DELIMITED, null);

                            }
                        });

        String sql = parameterizedSql
                .setParamValue(PARAM_NAME_NAME_COL_DELIMITED, this.sqlParamVals.get(PARAM_NAME_NAME_COL).toString())
                .setParamValue(PARAM_NAME_ID_COL_DELIMITED, this.sqlParamVals.get(PARAM_NAME_ID_COL).toString())
                .setParamValue(PARAM_NAME_COL_TABLE_ALIAS_DELIMITED, this.sqlParamVals.get(PARAM_NAME_COL_TABLE_ALIAS).toString())
                .setParamValue(PARAM_NAME_PAGE_SIZE_DELIMITED, this.sqlParamVals.get(PARAM_NAME_PAGE_SIZE).toString())
                .setParamValue(PARAM_NAME_FROM_CLAUSE_DELIMITED, this.sqlParamVals.get(PARAM_NAME_FROM_CLAUSE).toString())
                .setParamValue(PARAM_NAME_PAGE_NUMBER_COL_ALIAS_DELIMITED, getPageNumberColName())
                .getSql();

        return sql;
    }

    public String makeSinglePageSql() {


        String pageSqlTemplate;
        ParameterizedSql parameterizedSql;

        String nameColVal = this.sqlParamVals.get(PARAM_NAME_NAME_COL_VAL) != null ?
                this.sqlParamVals.get(PARAM_NAME_NAME_COL_VAL).toString() :
                null;
        String idColVal = this.sqlParamVals.get(PARAM_NAME_ID_COL_VAL) != null ?
                this.sqlParamVals.get(PARAM_NAME_ID_COL_VAL).toString() :
                null;

        // if it's the first page, we just apply the LIMIT without the where clause
        if (nameColVal != null && idColVal != null) {

            pageSqlTemplate = "select " + PARAM_NAME_SELECT_COLS_DELIMITED
                    + PARAM_NAME_FROM_CLAUSE_DELIMITED
                    + " where " +
                    " ( " +
                    PARAM_NAME_COL_TABLE_ALIAS_DELIMITED + "." + PARAM_NAME_NAME_COL_DELIMITED + ", " +
                    PARAM_NAME_COL_TABLE_ALIAS_DELIMITED + "." + PARAM_NAME_ID_COL_DELIMITED +
                    " ) > ( " +
                    " '" + PARAM_NAME_NAME_COL_VAL_DELIMITED + "', " +
                    PARAM_NAME_ID_COL_VAL_DELIMITED +
                    " ) " +
                    " order by " +
                    PARAM_NAME_COL_TABLE_ALIAS_DELIMITED + "." +PARAM_NAME_NAME_COL_DELIMITED + " , " +
                    PARAM_NAME_COL_TABLE_ALIAS_DELIMITED + "." +PARAM_NAME_ID_COL_DELIMITED +
                    " limit " + PARAM_NAME_PAGE_SIZE_DELIMITED;


            parameterizedSql =
                    new ParameterizedSql(pageSqlTemplate,
                            new HashMap<String, String>() {
                                {
                                    put(PARAM_NAME_NAME_COL_DELIMITED, null);
                                    put(PARAM_NAME_ID_COL_DELIMITED, null);
                                    put( PARAM_NAME_COL_TABLE_ALIAS_DELIMITED,null );
                                    put(PARAM_NAME_SELECT_COLS_DELIMITED, null);
                                    put(PARAM_NAME_PAGE_SIZE_DELIMITED, null);
                                    put(PARAM_NAME_FROM_CLAUSE_DELIMITED, null);
                                    put(PARAM_NAME_NAME_COL_VAL_DELIMITED, null);
                                    put(PARAM_NAME_ID_COL_VAL_DELIMITED, null);
                                }
                            });


            parameterizedSql.setParamValue(PARAM_NAME_NAME_COL_DELIMITED, this.sqlParamVals.get(PARAM_NAME_NAME_COL).toString())
                    .setParamValue(PARAM_NAME_ID_COL_DELIMITED, this.sqlParamVals.get(PARAM_NAME_ID_COL).toString())
                    .setParamValue(PARAM_NAME_COL_TABLE_ALIAS_DELIMITED, this.sqlParamVals.get(PARAM_NAME_COL_TABLE_ALIAS).toString())
                    .setParamValue(PARAM_NAME_SELECT_COLS_DELIMITED, this.sqlParamVals.get(PARAM_NAME_SELECT_COLS).toString())
                    .setParamValue(PARAM_NAME_PAGE_SIZE_DELIMITED, this.sqlParamVals.get(PARAM_NAME_PAGE_SIZE).toString())
                    .setParamValue(PARAM_NAME_FROM_CLAUSE_DELIMITED, this.sqlParamVals.get(PARAM_NAME_FROM_CLAUSE).toString())
                    .setParamValue(PARAM_NAME_NAME_COL_VAL_DELIMITED, this.sqlParamVals.get(PARAM_NAME_NAME_COL_VAL).toString())
                    .setParamValue(PARAM_NAME_ID_COL_VAL_DELIMITED, this.sqlParamVals.get(PARAM_NAME_ID_COL_VAL).toString());

        } else {

            pageSqlTemplate = "select " + PARAM_NAME_SELECT_COLS_DELIMITED
                    + PARAM_NAME_FROM_CLAUSE_DELIMITED +
                    " order by " +
                    PARAM_NAME_COL_TABLE_ALIAS_DELIMITED + "." + PARAM_NAME_NAME_COL_DELIMITED + " , " +
                    PARAM_NAME_COL_TABLE_ALIAS_DELIMITED + "." + PARAM_NAME_ID_COL_DELIMITED +
                    " limit " + PARAM_NAME_PAGE_SIZE_DELIMITED;

            parameterizedSql =
                    new ParameterizedSql(pageSqlTemplate,
                            new HashMap<String, String>() {
                                {
                                    put(PARAM_NAME_NAME_COL_DELIMITED, null);
                                    put(PARAM_NAME_ID_COL_DELIMITED, null);
                                    put(PARAM_NAME_COL_TABLE_ALIAS_DELIMITED,null);
                                    put(PARAM_NAME_SELECT_COLS_DELIMITED, null);
                                    put(PARAM_NAME_PAGE_SIZE_DELIMITED, null);
                                    put(PARAM_NAME_FROM_CLAUSE_DELIMITED, null);
                                }
                            });

            parameterizedSql.setParamValue(PARAM_NAME_NAME_COL_DELIMITED, this.sqlParamVals.get(PARAM_NAME_NAME_COL).toString())
                    .setParamValue(PARAM_NAME_ID_COL_DELIMITED, this.sqlParamVals.get(PARAM_NAME_ID_COL).toString())
                    .setParamValue(PARAM_NAME_COL_TABLE_ALIAS_DELIMITED, this.sqlParamVals.get(PARAM_NAME_COL_TABLE_ALIAS).toString())
                    .setParamValue(PARAM_NAME_SELECT_COLS_DELIMITED, this.sqlParamVals.get(PARAM_NAME_SELECT_COLS).toString())
                    .setParamValue(PARAM_NAME_PAGE_SIZE_DELIMITED, this.sqlParamVals.get(PARAM_NAME_PAGE_SIZE).toString())
                    .setParamValue(PARAM_NAME_FROM_CLAUSE_DELIMITED, this.sqlParamVals.get(PARAM_NAME_FROM_CLAUSE).toString());

        }


        String sql = parameterizedSql.getSql();

        return sql;
    }


} // class: PagerSql
