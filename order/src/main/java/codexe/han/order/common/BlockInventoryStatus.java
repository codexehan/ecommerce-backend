package codexe.han.order.common;

public enum BlockInventoryStatus {
    SUCCESS(10),
    FAILED(20),
    ASYNC(30),
    SYSTEM_ISSUE(40);

    private int status;

    BlockInventoryStatus(int status){
        this.status = status;
    }
}
