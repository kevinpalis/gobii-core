package org.gobiiproject.gobiidao.resultset.core.listquery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.gobiiproject.gobiidao.cache.PageFrameState;
import org.gobiiproject.gobiidao.cache.PageState;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.jdbc.Work;

/**
 */
public class ResultSetFromSqlPaged<T> implements Work {


    private ListStatementPaged listStatementPaged;
    private Integer pageSize;
    private Integer pageNo;
    //private String pgQueryId;
    private PageFrameState pageFrameState;

    public ResultSetFromSqlPaged(ListStatementPaged listStatementPaged,
                                 Integer pageSize,
                                 Integer pageNo,
                                 PageFrameState pageFrameState) {

        this.listStatementPaged = listStatementPaged;
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.pageFrameState = pageFrameState;

    }


    private ResultSet resultSet;

    public ResultSet getResultSet() {
        return resultSet;
    }

    public PageFrameState getPageFrameState() {
        return pageFrameState;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    @Override
    public void execute(Connection dbConnection) throws SQLException {


        if (this.pageFrameState == null) {

            this.pageFrameState = new PageFrameState(this.pageSize);

            PreparedStatement preparedStatement = listStatementPaged.makePreparedStatementForPageFrames(
                    dbConnection,
                    pageSize
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            this.pageFrameState = new PageFrameState(this.pageSize);

            while (resultSet.next()) {

                Integer idColVal = resultSet.getInt(listStatementPaged.getIdColName());
                String nameColVal = resultSet.getString(listStatementPaged.getNameColName());
                Integer pageNumber = resultSet.getInt(listStatementPaged.getPageNumberColName());

                PageState currentPageState = new PageState(pageNumber, nameColVal, idColVal);
                this.pageFrameState.getPages().add(currentPageState);
            }
        }

        String nameColVal = null;
        Integer idColVal = null;

        // our incoming page number is zero based, but in the page frame query result:
        // 1) There is no first page page definition -- the first page is just the first n records;
        // 2) The number is 1-based -- the first page has page number 2
        // So if we are requesting page 0, we want nameColVal and idCol val to be empty so that the query
        // will default over to the first N records; otherwise, we want to index into the page collectioin
        // and set nameColVal and idColVal accordingly
        if (this.pageNo > 0) {


            if ((this.pageNo - 1) <= (this.pageFrameState.getPages().size() - 1)) {

                Integer pageStateNo = this.pageNo - 1; // off by one more
                PageState pageState = this.pageFrameState.getPages().get(pageStateNo);


                nameColVal = pageState.getNameValue();
                idColVal = pageState.getIdValue();
            } else {
                String message = "The requested page " + this.pageNo + " exceeds the number of available pages " + this.pageFrameState.getPages().size();
                throw new SQLGrammarException(message, new SQLException(message), message);
            }

        }


        PreparedStatement preparedStatement = listStatementPaged.makePreparedStatementForAPage(dbConnection,
                this.pageSize,
                nameColVal,
                idColVal);

        this.resultSet = preparedStatement.executeQuery();


    } // execute()
}
