import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

public class Banker {
    private int[][] maxRes;
    private int[][] aloRes;
    private int[][] needRes;
    private int[] avaRes;
    private boolean[] markedProc;
    private int nRes;
    private int nProc;
    private Stack<Integer> stack;
    private ArrayList<String> listSafeSate = new ArrayList<>();
    private int sysStatus = 0;

    public Banker(){}
    public Banker(int[][] maxRes, int[][] aloRes, int[] avaRes){
        this.maxRes = maxRes;
        this.aloRes = aloRes;
        this.avaRes = avaRes;
        this.nProc = maxRes.length;
        this.nRes = maxRes[0].length;
        this.markedProc = new boolean[this.nProc];
        this.stack = new Stack<>();
        this.needRes = new int[this.nProc][this.nRes];
        if(isRightState(this.maxRes, this.aloRes)){
            for (int i = 0; i < this.nProc; i++)
                for (int j = 0; j < this.nRes; j++)
                    needRes[i][j] = maxRes[i][j] - aloRes[i][j];
            bankerOp();
        }
    }

    public Banker(int[][] maxRes, int[][] aloRes, int[] avaRes, int reqPro, int[] reqRes){
        this.maxRes = maxRes;
        this.aloRes = aloRes;
        this.avaRes = avaRes;
        this.nProc = maxRes.length;
        this.nRes = maxRes[0].length;
        this.markedProc = new boolean[this.nProc];
        this.stack = new Stack<>();
        for(int i = 0; i < this.nRes; i++){
            this.aloRes[reqPro-1][i] += reqRes[i];
        }
        if(isRightState(this.maxRes, this.aloRes)){
            this.needRes = new int[this.nProc][this.nRes];
            for (int i = 0; i < this.nProc; i++)
                for (int j = 0; j < this.nRes; j++)
                    this.needRes[i][j] = this.maxRes[i][j] - this.aloRes[i][j];
            boolean isEnoughRes = true;
            for(int i = 0; i< reqRes.length; i++){
                if(reqRes[i] > avaRes[i]){
                    isEnoughRes = false;
                    break;
                }
            }
            if(isEnoughRes){
                for(int i = 0; i< reqRes.length; i++){
                    this.avaRes[i] -= reqRes[i];
                }
                bankerOp();
            }else {
                this.sysStatus = 2;
            }
        }
    }

    private boolean isRightState(int[][] maxRes, int[][] aloRes){
        for(int i = 0; i < maxRes.length; i++){
            for(int j = 0; j < maxRes[0].length; j++){
                if(maxRes[i][j] < aloRes[i][j]){
                    this.sysStatus = 3;
                    return false;
                }
            }
        }
        return true;
    }

    public int getSystemStatus(){
        return this.sysStatus;
    }

    public ArrayList<String> getListSafeSate() {
        return listSafeSate;
    }

    private boolean isAvailable(int processId){
        //for each resource of process
        for (int i = 0; i < this.nRes; i++) {
            //if available not enough to need
            if (this.needRes[processId][i] > this.avaRes[i])
                return false;
        }
        return true;
    }

    public void bankerOp() {
        //for each process
        for (int i = 0; i < this.nProc; i++) {
            //if this process not yet allocated and available to provide resource
            if (!this.markedProc[i] && isAvailable(i)) {
                //mark this process as allocated
                this.markedProc[i] = true;
                //refill available resource
                for (int j = 0; j < this.nRes; j++)
                    this.avaRes[j] += this.aloRes[i][j];
                //push to state
                this.stack.push(i);
                //find next process to allocate
                bankerOp();

                //reset all flag and recover origin available resource
                this.stack.pop();
                this.markedProc[i] = false;
                for (int j = 0; j < this.nRes; j++)
                    this.avaRes[j] -= this.aloRes[i][j];
            }
        }
        //if find a safe state then add to list safe state
        if (this.stack.size() == this.nProc) {
            String state = "";
            for (int i = 0; i < this.nProc; i++) {
                state += "P" + (this.stack.get(i)+1);
                if (i != (this.nProc - 1))
                    state += " => ";
            }
            this.sysStatus = 1;
            this.listSafeSate.add(state);
        }
    }
}
