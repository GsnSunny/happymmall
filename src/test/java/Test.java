import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;

public class Test {
    public void Tianjisaima() {

        Scanner sc = new Scanner(System.in);
        char s[] = new char[10010];
        String str = sc.next();
        int i, ans, flag;
        ans = 0;
        flag = 0;
        Stack<Character> st = new Stack<Character>();
        for (i = 0; i < str.length(); i++) {
            if (s[i] == '(')
                st.push(s[i]);
            if (st.size() > ans)
                ans = st.size();
            if (st.size() == 0 && s[i] == ')') {
                flag = 1;
                break;
            }
            if (st.size() == i && s[i] == ')')
                st.pop();
        }
        if (flag == 1 || st.size() != 0)
            System.out.print("Error");
        else
            System.out.printf("%d\n", ans);


    }
}



