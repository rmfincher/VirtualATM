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

/** This is an auto generated class representing the Transaction type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Transactions", type = Model.Type.USER, version = 1)
public final class Transaction implements Model {
  public static final QueryField ID = field("Transaction", "id");
  public static final QueryField SENDER_USERNAME = field("Transaction", "senderUsername");
  public static final QueryField RECIPIENT_USERNAME = field("Transaction", "recipientUsername");
  public static final QueryField FUNDS = field("Transaction", "funds");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String senderUsername;
  private final @ModelField(targetType="String", isRequired = true) String recipientUsername;
  private final @ModelField(targetType="Float", isRequired = true) Double funds;
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
  
  public String getSenderUsername() {
      return senderUsername;
  }
  
  public String getRecipientUsername() {
      return recipientUsername;
  }
  
  public Double getFunds() {
      return funds;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Transaction(String id, String senderUsername, String recipientUsername, Double funds) {
    this.id = id;
    this.senderUsername = senderUsername;
    this.recipientUsername = recipientUsername;
    this.funds = funds;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Transaction transaction = (Transaction) obj;
      return ObjectsCompat.equals(getId(), transaction.getId()) &&
              ObjectsCompat.equals(getSenderUsername(), transaction.getSenderUsername()) &&
              ObjectsCompat.equals(getRecipientUsername(), transaction.getRecipientUsername()) &&
              ObjectsCompat.equals(getFunds(), transaction.getFunds()) &&
              ObjectsCompat.equals(getCreatedAt(), transaction.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), transaction.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getSenderUsername())
      .append(getRecipientUsername())
      .append(getFunds())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Transaction {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("senderUsername=" + String.valueOf(getSenderUsername()) + ", ")
      .append("recipientUsername=" + String.valueOf(getRecipientUsername()) + ", ")
      .append("funds=" + String.valueOf(getFunds()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static SenderUsernameStep builder() {
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
  public static Transaction justId(String id) {
    return new Transaction(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      senderUsername,
      recipientUsername,
      funds);
  }
  public interface SenderUsernameStep {
    RecipientUsernameStep senderUsername(String senderUsername);
  }
  

  public interface RecipientUsernameStep {
    FundsStep recipientUsername(String recipientUsername);
  }
  

  public interface FundsStep {
    BuildStep funds(Double funds);
  }
  

  public interface BuildStep {
    Transaction build();
    BuildStep id(String id);
  }
  

  public static class Builder implements SenderUsernameStep, RecipientUsernameStep, FundsStep, BuildStep {
    private String id;
    private String senderUsername;
    private String recipientUsername;
    private Double funds;
    public Builder() {
      
    }
    
    private Builder(String id, String senderUsername, String recipientUsername, Double funds) {
      this.id = id;
      this.senderUsername = senderUsername;
      this.recipientUsername = recipientUsername;
      this.funds = funds;
    }
    
    @Override
     public Transaction build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Transaction(
          id,
          senderUsername,
          recipientUsername,
          funds);
    }
    
    @Override
     public RecipientUsernameStep senderUsername(String senderUsername) {
        Objects.requireNonNull(senderUsername);
        this.senderUsername = senderUsername;
        return this;
    }
    
    @Override
     public FundsStep recipientUsername(String recipientUsername) {
        Objects.requireNonNull(recipientUsername);
        this.recipientUsername = recipientUsername;
        return this;
    }
    
    @Override
     public BuildStep funds(Double funds) {
        Objects.requireNonNull(funds);
        this.funds = funds;
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
    private CopyOfBuilder(String id, String senderUsername, String recipientUsername, Double funds) {
      super(id, senderUsername, recipientUsername, funds);
      Objects.requireNonNull(senderUsername);
      Objects.requireNonNull(recipientUsername);
      Objects.requireNonNull(funds);
    }
    
    @Override
     public CopyOfBuilder senderUsername(String senderUsername) {
      return (CopyOfBuilder) super.senderUsername(senderUsername);
    }
    
    @Override
     public CopyOfBuilder recipientUsername(String recipientUsername) {
      return (CopyOfBuilder) super.recipientUsername(recipientUsername);
    }
    
    @Override
     public CopyOfBuilder funds(Double funds) {
      return (CopyOfBuilder) super.funds(funds);
    }
  }
  

  public static class TransactionIdentifier extends ModelIdentifier<Transaction> {
    private static final long serialVersionUID = 1L;
    public TransactionIdentifier(String id) {
      super(id);
    }
  }
  
}
