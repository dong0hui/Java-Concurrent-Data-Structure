# 2.1 Depth First Searching \(DFS\)

## Warm-up example

**Given a collection of distinct integers, return all possible permutations.**

**Example:Input: \[1,2,3\]  
Output:  
\[  
  \[1,2,3\],  
  \[1,3,2\],  
  \[2,1,3\],  
  \[2,3,1\],  
  \[3,1,2\],  
  \[3,2,1\]  
\]**

```text
public class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        dfs(res, nums, 0);
        return res;
    }
    private void dfs(List<List<Integer>> res, int[] nums, int j) {
        if (j == nums.length) {
            List<Integer> temp = new ArrayList<Integer>();
            for (int num : nums) temp.add(num);
            res.add(temp);
        }
        for (int i = j; i < nums.length; i++) {
            swap(nums, i, j);
            dfs(res, nums, j+1);
            swap(nums, i, j);
        }
    }
    private void swap(int[] nums, int m, int n) {
        int temp = nums[m];
        nums[m] = nums[n];
        nums[n] = temp;
    }
}
```

First, function _swap_ is used to swap nums\[m\] and nums\[n\]. Now we can forget Line 19-23. In function _permute,_ we find only function _dfs_ is significant because it is not definition \(Line 3\)  or return clause \(Line 5\). DFS is recursive, which means f\(x\) changes value of x and plugs new value of x into f\(x\) until stop condition is satisfied. 

