package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskDataAccess {

    private final String filePath;

    private final UserDataAccess userDataAccess;

    public TaskDataAccess() {
        filePath = "app/src/main/resources/tasks.csv";
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     * @param userDataAccess
     */
    public TaskDataAccess(String filePath, UserDataAccess userDataAccess) {
        this.filePath = filePath;
        this.userDataAccess = userDataAccess;
    }

    /**
     * CSVから全てのタスクデータを取得します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @return タスクのリスト
     */
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // ヘッダー行をスキップ
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
    
                if (values.length != 4) {
                    System.out.println("不正な行: " + line);
                    continue;
                }
                try {
                    int code = Integer.parseInt(values[0]);
                    String name = values[1];
                    int status = Integer.parseInt(values[2]);
                    int repUserCodeValue = Integer.parseInt(values[3]); // 担当者のコードを取得
                    User repUserCode = userDataAccess.findByCode(repUserCodeValue); // 担当者のユーザーを取得
    
                    if (repUserCode == null) {
                        System.out.println("ユーザーコードが見つかりません: " + repUserCodeValue);
                        continue; // ユーザーが見つからない場合はスキップ
                    }
    
                    Task task = new Task(code, name, status, repUserCode);
                    tasks.add(task);
                } catch (NumberFormatException e) {
                    System.out.println("数値変換エラー: " + e.getMessage() + " 行: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("ファイル読み込みエラー: " + e.getMessage());
            e.printStackTrace();
        }
        return tasks;
    }

    /**
     * タスクをCSVに保存します。
     * @param task 保存するタスク
     */
    public void save(Task task) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            String line = createLine(task);
            writer.newLine();
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを1件取得します。
     * @param code 取得するタスクのコード
     * @return 取得したタスク
     */
    public Task findByCode(int code) {
        Task task = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // ヘッダー行をスキップ
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != 4) {
                    // 無効な行をスキップ
                    continue;
                }
                try {
                    int codenum = Integer.parseInt(values[0]);
                    if (codenum == code) {
                        String name = values[1];
                        int status = Integer.parseInt(values[2]);
                        int repUserCodeValue = Integer.parseInt(values[3]);
                        User repUser = userDataAccess.findByCode(repUserCodeValue);
                        
                        task = new Task(codenum, name, status, repUser);
                        break; // 一致するタスクが見つかったのでループを終了
                    }
                } catch (NumberFormatException e) {
                    System.out.println("数値形式のエラーが発生しました: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // タスクが見つからなかった場合のログ
        // if (task == null) {
        
        // }
    
        return task;
    }
    

    /**
     * タスクデータを更新します。
     * @param updateTask 更新するタスク
     */
    public void update(Task updateTask) {
        List<Task> tasks = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Code,Name,Status,Rep_User_Code");
            writer.newLine();
            String line;
            for(Task task : tasks){
                if(task.getCode() == updateTask.getCode()){
                    line = createLine(updateTask);
                }else{
                    line = createLine(task);
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを削除します。
     * @param code 削除するタスクのコード
     */
    // public void delete(int code) {
    //     try () {

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    /**
     * タスクデータをCSVに書き込むためのフォーマットを作成します。
     * @param task フォーマットを作成するタスク
     * @return CSVに書き込むためのフォーマット文字列
     */
    private String createLine(Task task){
        return task.getCode() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getRepUser().getCode();
    }
}