/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

James G Willmore - LJ Computing - (C) 2023
*/
package net.ljcomputing.insuranceeip.constants;

/** Insured SQL statements. */
public enum InsuredSql {
    SELECT_COUNT("select count(id) as \"cnt\" from insured"),
    SELECT_ALL("select id, given_name, middle_name, surname, suffix from insured"),
    INSERT(
            "insert into insured (given_name, middle_name, surname, suffix)"
                    + " values(:#${body.givenName}, :#${body.middleName}, :#${body.surname},"
                    + " :#${body.suffix})");

    private String sql;

    /**
     * Constructor.
     *
     * @param sql
     */
    private InsuredSql(String sql) {
        this.sql = sql;
    }

    /**
     * SQL statement value.
     *
     * @return
     */
    public String sql() {
        return sql;
    }

    /**
     * SQL statement value with data source value appended to create a Camel Route URI.
     *
     * @param dataSource
     * @return
     */
    public String sql(String dataSource) {
        return String.format("%s?dataSource=#%s", sql, dataSource);
    }
}
