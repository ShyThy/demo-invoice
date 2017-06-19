package com.oxchains.billing.util;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.oxchains.billing.domain.Bill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static java.util.Optional.empty;

/**
 * @author aiet
 */
public class ResponseUtil {

  static final Logger LOG = LoggerFactory.getLogger(ResponseUtil.class);

  public static Optional<String> extract(String json, String path){
    try{
      JsonNode root = new ObjectMapper().readTree(json);
      return Optional.ofNullable(root.at("/data/token").textValue());
    }catch (Exception e){
      LOG.error("failed to extract value under path {} out of {}: {}", path, json, e.getMessage());
    }
    return empty();
  }

  public static String payloadToBill(String fabricManageResponse) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(mapper.readValue(fabricManageResponse, RestResp.class));
    } catch (Exception e) {
      LOG.error("failed to convert fabric-manage response: {}", e.getMessage());
    }
    return fabricManageResponse;
  }

  private static class RestResp {
    int status;
    String message;

    @JsonDeserialize(using = BillsDeserializer.class)
    Object data;

    public int getStatus() {
      return status;
    }

    public void setStatus(int status) {
      this.status = status;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public Object getData() {
      return data;
    }

    public void setData(Object data) {
      this.data = data;
    }
  }

  static class BillsDeserializer extends JsonDeserializer<Object> {

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      Data data = jp.readValueAs(Data.class);
      return data.getPayload();
    }

  }

  interface BillCompatible {
    Bill toBill();
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class Data {
    Object payload;
    String txid;

    public Object getPayload() {
      return payload;
    }

    private <T extends BillCompatible> Optional<Object> readAsBill(ObjectMapper mapper, String raw, Class<T> clazz) {
      try {
        return Optional.of(mapper.readValue(raw, clazz).toBill());
      } catch (Exception e) {
        LOG.error("failed to parse bill record {}: {}", raw, e.getMessage());
      }
      return empty();
    }

    public void setPayload(String payload) {
      List<Object> bills = new ArrayList<>();
      if (!"[],[],[]".equals(payload)) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
          JsonNode rootNode = objectMapper.readTree("[" + payload + "]");
          for (JsonNode node : rootNode) {
            if (node.isArray()) {
              node.forEach(rawRecord -> {
                if(rawRecord.isObject()) {
                  readAsBill(objectMapper, rawRecord.toString(), BillRecord.class).ifPresent(bills::add);
                }else {
                  bills.add(rawRecord.textValue());
                }
              });
            } else if (node.isObject()){
                this.payload = readAsBill(objectMapper, node.toString(), Record.class).orElse(emptyMap());
                return;
            }
          }
        } catch (Exception e) {
          LOG.error("failed to parse payload {}: {}", payload, e.getMessage());
        }
      }
      this.payload = bills;
    }

    public String getTxid() {
      return txid;
    }

    public void setTxid(String txid) {
      this.txid = txid;
    }
  }

  private static class Dummy {
  }

  private static class BillRecord implements BillCompatible {

    String key;
    Record record;

    public String getKey() {
      return key;
    }

    @JsonAlias("Key")
    public void setKey(String key) {
      this.key = key;
    }

    public Record getRecord() {
      return record;
    }

    @JsonAlias("Record")
    public void setRecord(Record record) {
      this.record = record;
    }

    @JsonIgnore
    public boolean hasContent() {
      return null != key && null != record;
    }

    public Bill toBill() {
      return recordToBill(getRecord(), getKey());
    }

  }

  private static Bill recordToBill(Record record, String... id) {
    Bill bill = new Bill();
    bill.setDue(record.getDueDate());
    bill.setDate(record.getCreateTime());
    bill.setPrice(record.getPrice());
      /* payee should be the owner or the payee */
    bill.setPayee(record.getPayeeName());
    bill.setDrawee(record.getPayerName());
    bill.setDrawer(record.getCreatorName());
    bill.setTransferable(record.getForbidTransfer());
    if (id != null && id.length > 0) {
      bill.setId(id[0].replace("BillStruct", ""));
    }
    //TODO map states to bill status
    bill.setStatus("");
    return bill;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class Record implements BillCompatible {

    public Bill toBill() {
      return recordToBill(this);
    }

    String billState;
    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss")
    Date createTime;
    String creatorName;
    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss")
    Date dueDate;
    String finishState;
    String forbidTransfer;
    String ownerName;
    String payeeName;
    String payerName;
    String payeeState;
    String payerState;
    String phase;
    String price;
    String transferState;
    String warrantor;
    String warrantorState;

    public String getPayerName() {
      return payerName;
    }

    public void setPayerName(String payerName) {
      this.payerName = payerName;
    }

    public Date getCreateTime() {
      return createTime;
    }

    public void setCreateTime(Date createTime) {
      this.createTime = createTime;
    }

    public Date getDueDate() {
      return dueDate;
    }

    public void setDueDate(Date dueDate) {
      this.dueDate = dueDate;
    }

    public String getBillState() {
      return billState;
    }

    public void setBillState(String billState) {
      this.billState = billState;
    }

    public String getCreatorName() {
      return creatorName;
    }

    public void setCreatorName(String creatorName) {
      this.creatorName = creatorName;
    }

    public String getFinishState() {
      return finishState;
    }

    public void setFinishState(String finishState) {
      this.finishState = finishState;
    }

    public String getForbidTransfer() {
      return forbidTransfer;
    }

    public void setForbidTransfer(String forbidTransfer) {
      this.forbidTransfer = forbidTransfer;
    }

    public String getOwnerName() {
      return ownerName;
    }

    public void setOwnerName(String ownerName) {
      this.ownerName = ownerName;
    }

    public String getPayeeName() {
      return payeeName;
    }

    public void setPayeeName(String payeeName) {
      this.payeeName = payeeName;
    }

    public String getPayeeState() {
      return payeeState;
    }

    public void setPayeeState(String payeeState) {
      this.payeeState = payeeState;
    }

    public String getPayerState() {
      return payerState;
    }

    public void setPayerState(String payerState) {
      this.payerState = payerState;
    }

    public String getPhase() {
      return phase;
    }

    public void setPhase(String phase) {
      this.phase = phase;
    }

    public String getPrice() {
      return price;
    }

    public void setPrice(String price) {
      this.price = price;
    }

    public String getTransferState() {
      return transferState;
    }

    public void setTransferState(String transferState) {
      this.transferState = transferState;
    }

    public String getWarrantor() {
      return warrantor;
    }

    public void setWarrantor(String warrantor) {
      this.warrantor = warrantor;
    }

    public String getWarrantorState() {
      return warrantorState;
    }

    public void setWarrantorState(String warrantorState) {
      this.warrantorState = warrantorState;
    }
  }


}