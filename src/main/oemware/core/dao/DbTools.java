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
import java.sql.DriverManager;

/**
 * The db tools object.
 *
 * @author Ryan Nitz
 * @version $Id: DbTools.java 13 2008-06-15 19:43:04Z oemware $
 */
public final class DbTools {

    /**
     * Create a new db connection. You must insert the JDBC driver jar your classpath.<br/><br/>
     * 
     * Sample Url: jdbc:mysql://localhost/DATABASE_NAME?autoReconnect=true&useUnicode=true&characterEncoding=utf8<br/>
     * Samble Driver: com.mysql.jdbc.Driver<br/><br/>
     * 
     * @param pUsername The username.
     * @param pPassword The password.
     * @param pUrl The JDBC url.
     * @param pDriver The driver name.
     * @throws DaoException
     */
    public static final Connection getConnection(   final String pUsername, 
                                                    final String pPassword, 
                                                    final String pUrl, 
                                                    final String pDriver)  
        throws DaoException                                                 
    {
        try {
            Class.forName (pDriver).newInstance();
            return DriverManager.getConnection (pUrl, pUsername, pPassword);
        } catch (ClassNotFoundException cnfe)  { throw new DaoException(cnfe);
        } catch (IllegalAccessException iae)  { throw new DaoException(iae);
        } catch (InstantiationException ie)  { throw new DaoException(ie);
        } catch (SQLException se) { throw new DaoException(se); }
    }

    /** 
     * Returns the current timestamp object.
     * @return The current timestamp.
     */
    public static final Timestamp getNow() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Close the JDBC objects.
     * @param pResultSet The result set.
     * @param pStatement The statement.
     * @param pConnection The connection.
     * @throws DaoException
     */
    public static final void close( final ResultSet pResultSet,
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
    public static final void close( final ResultSet pResultSet,
                                    final Statement pStatement)
        throws DaoException
    {
        closeResultSet(pResultSet);
        closeStatement(pStatement);
    }

    /**
     * Close the JDBC objects.
     * @param pStatement The statement.
     * @param pConnection The connection.
     * @throws DaoException
     */
    public static final void close( final Statement pStatement,
                                    final Connection pConnection)
        throws DaoException
    { closeStatement(pStatement); closeConnection(pConnection); }

    /**
     * Close the connection.
     * @param pConnection The connection.
     * @throws DaoException
     */
    public static final void closeConnection(final Connection pConnection)
        throws DaoException
    {
        if (pConnection == null) return;
        try { pConnection.close();
        } catch (SQLException sqle) { throw new DaoException(sqle); }
    }

    /**
     * Close the statement.
     * @param pStatement The statement.
     * @throws DaoException
     */
    public static final void closeStatement(final Statement pStatement)
        throws DaoException
    {
        if (pStatement == null) return;
        try { pStatement.close();
        } catch (SQLException sqle) { throw new DaoException(sqle); }
    }

    /**
     * Close the result set.
     * @param pResultSet The result set.
     * @throws DaoException
     */
    public static final void closeResultSet(final ResultSet pResultSet)
        throws DaoException
    {
        if (pResultSet == null) return;
        try { pResultSet.close();
        } catch (SQLException sqle) { throw new DaoException(sqle); }
    }
}

