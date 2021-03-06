package com.UniPlan.loginregister.Notice_B;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.UniPlan.loginregister.R;
import com.UniPlan.loginregister.Subject_;
import com.UniPlan.loginregister.Table;
import com.UniPlan.loginregister.ViewHolder;
import com.UniPlan.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.otaliastudios.zoom.ZoomLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;

public class Post_Treeview extends AppCompatActivity {
    /*
    [ 20210823 장준승 ] 게시판 연동 구현
    사용자의 ID를 게시판에서 Intent로 가져와서, Frag2에서 했던 것과 동일하게 띄워줍니다.
     */
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    String writerId, writerNick, forum_sort, post_id;
    TextView tv_post; // 상단 바 텍스트뷰

    // [ 장준승 ] TreeView 바로 보이도록 구현
    ZoomLayout zoomLayout;
    TreeView treeView;
    BaseTreeAdapter adapter;
    ArrayList<Subject_> subjectList = new ArrayList<>();
    TreeNode[] treeNodeList;
    ArrayList<Integer> adj[];
    HashMap<String, Integer> m;
    Table userTableInfo;
    TreeNode rootNode;

    //크기 유동적 변화 구현
    ViewHolder[] viewHolderList;
    private int displaySize = 500;
    private float displayHeight = 0;
    private float displayWidth = 0;
    private int displayWidthMargin = 1200;
    private int displayHeightMargin = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_treeview);

        Intent intent = getIntent();
        writerId = intent.getStringExtra("writerID");
        writerNick = intent.getStringExtra("writerNickname");
        forum_sort = intent.getStringExtra("forum_sort");
        post_id = intent.getStringExtra("post_id");

        tv_post = findViewById(R.id.tv_post_treeview);
        tv_post.setText(writerNick + "님의 커리큘럼");

        int subjectNumber = 200;
        viewHolderList = new ViewHolder[subjectNumber];

        zoomLayout = findViewById(R.id.post_zoom);
        treeView = new TreeView(getApplicationContext()) {
            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent event, float distanceX, float distanceY) {
                return false;
            }
        };
        treeView.setLevelSeparation(50);
        treeView.setLineColor(Color.BLACK);
        treeView.setLineThickness(5);
        adapter = new BaseTreeAdapter<ViewHolder>(getApplicationContext(), R.layout.node) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(View view) {
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
                /*
                [장준승] treenode에 정보를 업데이트 할 때, 오픈소스의 특성상 textview의
                        값 자체를 변환하기 어려우므로, String 값 자체에 모든 정보를 일괄적으로 넘겨주어 처리합니다.

                        Ex) 논리회로.1학년 1학기.1 (논리회로를 1학년 1학기에 듣고, 선택되었다.)
                 */

                String[] nodeData = data.toString().split("\\.");
                viewHolder.mTextView.setText(nodeData[0]);

                viewHolderList[m.get(nodeData[0])] = viewHolder;

                if (nodeData[2].equals("1") && nodeData[2] != null) {
                    viewHolder.setViewHolderSelected();
                } else {
                    viewHolder.setViewHoldernotSelected();
                }

                if (nodeData[1] != null) {
                    viewHolder.semesterTv.setText(nodeData[1]);
                } else {
                    viewHolder.semesterTv.setText("인식 오류");
                }
                viewHolder.setSemesterColored();

                /*
                각 노드 선택시 BottomSheetDialog 띄우는 작업 수행
                 */
            }
        };
        treeView.setAdapter(adapter);

        getSubjectListFromFB();
    }

    public void getSubjectListFromFB() {
        mStore.collection("Subject")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                subjectList.add(document.toObject(Subject_.class));
                            }
                        } else {
                        }
                        treeNodeList = new TreeNode[subjectList.size()];

                        //adj 초기화
                        adj = new ArrayList[subjectList.size()];
                        for (int i = 0; i < subjectList.size(); i++)
                            adj[i] = new ArrayList<Integer>();

                        mappingSubjectList();
                        getTableFromFB();
                    }
                });
    }

    public void mappingSubjectList() {
        /* DB에서 받아온 과목들 매핑 */
        m = new HashMap<String, Integer>();
        for (int i = 0; i < subjectList.size(); i++) {
            m.put(subjectList.get(i).getName(), m.size());
        }
    }

    public void getTableFromFB() {
        docRef = mStore.collection(forum_sort).document(post_id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);

                userTableInfo = post.getTable();
                Log.e("###", "UserTableInfo : " + userTableInfo.getRoot().toString());
                changeToAdj(userTableInfo);

                if (userTableInfo == null) {
                    Toast.makeText(getApplicationContext(), "트리를 추가해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void changeToAdj(Table table) {
        for (String currSubject : table.getTable().keySet()) {
            Map<String, String> currRow = table.getTable().get(currSubject);

            for (String nextSubject : currRow.keySet()) {
                if (!currRow.get(nextSubject).equals("0")) {
                    int currMappingPos = m.get(currSubject);
                    int nextMappingPos = m.get(nextSubject);

                    adj[currMappingPos].add(nextMappingPos);
                }
            }
        }

        //Table의 root 값으로 루트노드 설정 후 adj로 트리 만들기
        rootNode = new TreeNode(table.getRoot());
        treeNodeList[m.get(table.getRoot().split("\\.")[0])] = rootNode;

        makeTreeByAdj(rootNode);
        adapter.setRootNode(rootNode);

        Log.e("###", "zoomLayout Test : " + zoomLayout.toString());
        zoomLayout.removeAllViews();
        zoomLayout.addView(treeView);

        updateDisplaySize();
    }

    // adj로 트리 만들기
    public void makeTreeByAdj(TreeNode currNode) {
        String currSubjectName = currNode.getData().toString().split("\\.")[0];
        int currMappingPos = m.get(currSubjectName);

        for (int nextMappingPos : adj[currMappingPos]) {
            String nextSubjectName = subjectList.get(nextMappingPos).getName();
            final TreeNode newChild = new TreeNode(nextSubjectName + userTableInfo.getTable().get(currSubjectName).get(nextSubjectName));
            treeNodeList[nextMappingPos] = newChild;

            currNode.addChild(newChild);
            makeTreeByAdj(newChild);
        }
    }

    public void updateDisplaySize() {
        treeView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                for (ViewHolder viewHolder : viewHolderList) {
                    if (viewHolder != null) {
                        if (displayHeight < viewHolder.tn_layout.getY()) {
                            displayHeight = viewHolder.tn_layout.getY();
                        }
                        if (displayWidth < viewHolder.tn_layout.getX()) {
                            displayWidth = viewHolder.tn_layout.getX();
                        }
                    }
                }
                treeView.setMinimumWidth((int) displayWidth + displayWidthMargin);
                treeView.setMinimumHeight((int) displayHeight + displayHeightMargin);

                treeView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });

        zoomLayout.moveTo((float) 1.0, 0, 0, false);
        zoomLayout.zoomBy((float) 1.0, false);
        zoomLayout.zoomOut();

        return;
    }
}