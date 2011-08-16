/**
 * (C) Copyright 2007, Deft Labs.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oemware.core.dao;

// Java
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.DataSource;

/**
 * The base dao object. If the reuse connection is set to
 * true the object is no longer thread safe.
 *
 * @author Ryan Nitz
 * @version $Id: BaseDao.java 13 2008-06-15 19:43:04Z oemware $
 */
public abstract class BaseDao {

    protected boolean mReuseConn = false;
    protected Connection mConn;
    protected DataSource mDataSource;

    public BaseDao() { }
    public BaseDao(final DataSource pDataSource) { mDataSource = pDataSource; }

    /**
     * Returns a connection from the data source.
     * @throws DaoException
     */
    protected final Connection getConnection() throws DaoException {
        if (mDataSource == null) throw new DaoException("null data source");
        try {
            if (!mReuseConn) return mDataSource.getConnection();
            if (mConn == null) mConn = mDataSource.getConnection();
        } catch (SQLException sqle) { throw new DaoException(sqle); }
        return mConn;
    }

    /**
     * Returns a connection.
     * @param pUsername The username.
     * @param pUrl The url.
     * @param pDriver The driver.
     */
    protected final Connection getConnection(   final String pUsername, 
                                                final String pPassword, 
                                                final String pUrl, 
                                                final String pDriver)  
        throws DaoException                                                 
    { 
        if (!mReuseConn)
        { return DbTools.getConnection(pUsername, pPassword, pUrl, pDriver); }
        
        if (mConn == null)  
        { mConn = DbTools.getConnection(pUsername, pPassword, pUrl, pDriver); }
        return mConn;
    }

    /** 
     * Returns the current timestamp object.
     * @return The current timestamp.
     */
    protected final Timestamp getNow() { return DbTools.getNow(); }

    /**
     * Close the JDBC objects.
     * @param pResultSet The result set.
     * @param pStatement The statement.
     * @param pConnection The connection.
     * @throws DaoException
     */
    protected final void close( final ResultSet pResultSet,
                                final Statement pStatement,
                                final Connection pConnection)
        throws DaoException
    { 
        closeResultSet(pResultSet); 
        closeStatement(pStatement); 
        closeConnection(pConnection); 
    }

    /**
     * Close the JDBC objects.
     * @param pResultSet The result set.
     * @param pStatement The statement.
     * @throws DaoException
     */
    protected final void close( final ResultSet pResultSet,
                                final Statement pStatement)
        throws DaoException
    { DbTools.close(pResultSet, pStatement); }

    /**
     * Close the JDBC objects.
     * @param pStatement The statement.
     * @param pConnection The connection.
     * @throws DaoException
     */
    protected final void close( final Statement pStatement,
                                final Connection pConnection)
        throws DaoException
    { closeStatement(pStatement); closeConnection(pConnection); }

    /**
     * Close the JDBC connection.
     * @throws DaoException
     */
    protected final void closeConnection() throws DaoException {
        if (!mReuseConn) throw new IllegalStateException("not reusing conn");
        if (mConn == null) return;
        DbTools.closeConnection(mConn);
    }

    /**
     * Close the JDBC connection.
     * @param pConnection The connection.
     * @throws DaoException
     */
    protected final void closeConnection(final Connection pConnection)
        throws DaoException
    { 
        if (!mReuseConn) { DbTools.closeConnection(pConnection); }
    }

    /**
     * Close the statement.
     * @param pStatement The statement.
     * @throws DaoException
     */
    protected final void closeStatement(final Statement pStatement)
        throws DaoException
    { DbTools.closeStatement(pStatement); }

    /**
     * Close the result set.
     * @param pResultSet The result set.
     * @throws DaoException
     */
    protected final void closeResultSet(final ResultSet pResultSet)
        throws DaoException
    { DbTools.closeResultSet(pResultSet); }

    /**
     * Enable connection reuse. Defautl is false.
     * @param pV Set to true to reuse.
     */
    protected final void setReuseConn(final boolean pV) { mReuseConn = pV; }
}

