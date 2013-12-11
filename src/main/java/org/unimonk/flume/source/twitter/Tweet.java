/* 
 * Author : Magham Ravi Kiran
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
* may not use this file except in compliance with the License. You may
* obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0 
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
* implied. See the License for the specific language governing
* permissions and limitations under the License.
*/
package org.unimonk.flume.source.twitter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.text.StrBuilder;


public class Tweet implements Serializable {

    private final static String KEY_DELIMITER = "\t";
    private final java.text.SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Long      id;
    private Long    userId;
    private String    text;
    private Double    latitude;
    private Double    longitude;
    private Timestamp createdAt;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double lattitude) {
        this.latitude = lattitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Timestamp createdAt) {
       this.createdAt = createdAt;
    }
   
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Tweet other = (Tweet)obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (userId == null) {
            if (other.userId != null) return false;
        } else if (!userId.equals(other.userId)) return false;
        return true;
    }
    @Override
    public String toString() {
            
        StrBuilder st = new StrBuilder();
        return st.append(id)
          .appendSeparator(KEY_DELIMITER)
          .append(userId)
          .appendSeparator(KEY_DELIMITER)
          .append(latitude == null ? "" : latitude)
          .appendSeparator(KEY_DELIMITER)
          .append(longitude == null ? "" : longitude)
          .appendSeparator(KEY_DELIMITER)
          .append(dateformat.format(createdAt))
          .appendSeparator(KEY_DELIMITER)
          .append(text)
         .toString();
        
    }

}
