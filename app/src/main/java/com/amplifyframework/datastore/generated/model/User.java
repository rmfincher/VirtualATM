package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.ModelIdentifier;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the User type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Users", type = Model.Type.USER, version = 1)
public final class User implements Model {
  public static final QueryField ID = field("User", "id");
  public static final QueryField USERNAME = field("User", "username");
  public static final QueryField FUNDS = field("User", "funds");
  public static final QueryField LONGITUDE = field("User", "longitude");
  public static final QueryField LATITUDE = field("User", "latitude");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String username;
  private final @ModelField(targetType="Float", isRequired = true) Double funds;
  private final @ModelField(targetType="Float", isRequired = true) Double longitude;
  private final @ModelField(targetType="Float", isRequired = true) Double latitude;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public String getUsername() {
      return username;
  }
  
  public Double getFunds() {
      return funds;
  }
  
  public Double getLongitude() {
      return longitude;
  }
  
  public Double getLatitude() {
      return latitude;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private User(String id, String username, Double funds, Double longitude, Double latitude) {
    this.id = id;
    this.username = username;
    this.funds = funds;
    this.longitude = longitude;
    this.latitude = latitude;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      User user = (User) obj;
      return ObjectsCompat.equals(getId(), user.getId()) &&
              ObjectsCompat.equals(getUsername(), user.getUsername()) &&
              ObjectsCompat.equals(getFunds(), user.getFunds()) &&
              ObjectsCompat.equals(getLongitude(), user.getLongitude()) &&
              ObjectsCompat.equals(getLatitude(), user.getLatitude()) &&
              ObjectsCompat.equals(getCreatedAt(), user.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), user.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUsername())
      .append(getFunds())
      .append(getLongitude())
      .append(getLatitude())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("User {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("username=" + String.valueOf(getUsername()) + ", ")
      .append("funds=" + String.valueOf(getFunds()) + ", ")
      .append("longitude=" + String.valueOf(getLongitude()) + ", ")
      .append("latitude=" + String.valueOf(getLatitude()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static UsernameStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static User justId(String id) {
    return new User(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      username,
      funds,
      longitude,
      latitude);
  }
  public interface UsernameStep {
    FundsStep username(String username);
  }
  

  public interface FundsStep {
    LongitudeStep funds(Double funds);
  }
  

  public interface LongitudeStep {
    LatitudeStep longitude(Double longitude);
  }
  

  public interface LatitudeStep {
    BuildStep latitude(Double latitude);
  }
  

  public interface BuildStep {
    User build();
    BuildStep id(String id);
  }
  

  public static class Builder implements UsernameStep, FundsStep, LongitudeStep, LatitudeStep, BuildStep {
    private String id;
    private String username;
    private Double funds;
    private Double longitude;
    private Double latitude;
    public Builder() {
      
    }
    
    private Builder(String id, String username, Double funds, Double longitude, Double latitude) {
      this.id = id;
      this.username = username;
      this.funds = funds;
      this.longitude = longitude;
      this.latitude = latitude;
    }
    
    @Override
     public User build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new User(
          id,
          username,
          funds,
          longitude,
          latitude);
    }
    
    @Override
     public FundsStep username(String username) {
        Objects.requireNonNull(username);
        this.username = username;
        return this;
    }
    
    @Override
     public LongitudeStep funds(Double funds) {
        Objects.requireNonNull(funds);
        this.funds = funds;
        return this;
    }
    
    @Override
     public LatitudeStep longitude(Double longitude) {
        Objects.requireNonNull(longitude);
        this.longitude = longitude;
        return this;
    }
    
    @Override
     public BuildStep latitude(Double latitude) {
        Objects.requireNonNull(latitude);
        this.latitude = latitude;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String username, Double funds, Double longitude, Double latitude) {
      super(id, username, funds, longitude, latitude);
      Objects.requireNonNull(username);
      Objects.requireNonNull(funds);
      Objects.requireNonNull(longitude);
      Objects.requireNonNull(latitude);
    }
    
    @Override
     public CopyOfBuilder username(String username) {
      return (CopyOfBuilder) super.username(username);
    }
    
    @Override
     public CopyOfBuilder funds(Double funds) {
      return (CopyOfBuilder) super.funds(funds);
    }
    
    @Override
     public CopyOfBuilder longitude(Double longitude) {
      return (CopyOfBuilder) super.longitude(longitude);
    }
    
    @Override
     public CopyOfBuilder latitude(Double latitude) {
      return (CopyOfBuilder) super.latitude(latitude);
    }
  }
  

  public static class UserIdentifier extends ModelIdentifier<User> {
    private static final long serialVersionUID = 1L;
    public UserIdentifier(String id) {
      super(id);
    }
  }
  
}
