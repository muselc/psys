package org.ye.psys.core.config;

/**
 * @Author liansongye
 * @create 2019-04-29 09:35
 */
public class Distinguish {
    private String LogisticCode;
    private Shippers shippers;
    public class Shippers {
        private String ShipperName;
        private String ShipperCode;

        public String getShipperName() {
            return ShipperName;
        }

        public void setShipperName(String shipperName) {
            ShipperName = shipperName;
        }

        public String getShipperCode() {
            return ShipperCode;
        }

        public void setShipperCode(String shipperCode) {
            ShipperCode = shipperCode;
        }
    }
    private String EBusinessID;
    private String Code;
    private boolean Success;

    public String getLogisticCode() {
        return LogisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        LogisticCode = logisticCode;
    }

    public Shippers getShippers() {
        return shippers;
    }

    public void setShippers(Shippers shippers) {
        this.shippers = shippers;
    }

    public String getEBusinessID() {
        return EBusinessID;
    }

    public void setEBusinessID(String EBusinessID) {
        this.EBusinessID = EBusinessID;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }
}
