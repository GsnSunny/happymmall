import java.util.*;

public class Test1 {
    public static final Integer MAX = 100000;
    static int[] dp = new int[MAX];
    static int[] p = new int[MAX];
    static int num;
    static boolean[] s  = new boolean[MAX];
    static int n,m;

   public static void prime(){
        for(int i=0;i<MAX;i++) s[i]=false;
        for(int i=2;i*i<MAX;i++){
            if(!s[i]){
                for(int j=2;j*i<MAX;j++ )
                    s[i*j]=true;
            }
        }
        num=0;
        for(int i=2;i<MAX;i++)
            if(!s[i]) p[num++]=i;
    }

   public static void init(){
        dp[1]=1; dp[0]=0;
        for(int i=2;i<MAX;i++){
            double m=(double)i;
            double k=Math.ceil(Math.pow(m,1.0/2));
            if(Math.pow(k,2.0)==m) dp[i]=1;
            dp[i]+=dp[i-1];
        }
    }

    public static void main(String[] args) {
        init();
        prime();
        Scanner sc = new Scanner(System.in);
        n = sc.nextInt();
        m = sc.nextInt();
        int sum=dp[m];
        for(int i=2;i<=n;i++){
            if(!s[i]) {
                if(i>m) continue;
                sum += dp[m/i];
                continue;
            }
            int d=i,r=1;
            for(int j=0;p[j]*p[j]<=d;j++){
                int f=0;
                while(d%p[j]==0){
                    f++;
                    d/=p[j];
                }
                if(f%2==1) r*=p[j];
            }
            if(d>1) r*=d;
            if(r>m) continue;
            sum += dp[m/r];
        }
        System.out.println(sum);
    }
}