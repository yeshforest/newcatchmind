package org.example;

import javax.swing.*;
import java.awt.*;
/*
* JPanel을 상속받아 만든 유저 상태창 클래스
* */
public class UserStatusPanel extends JPanel{
    private JLabel lblUserName; // 유저 이름
    private JLabel lblScore; // 유저 점수

    public UserStatusPanel(int y){
        this.setBackground(new Color(0,0,100));
        int X = 6;
        int WIDTH = 169;
        int HEIGHT = 61;
        this.setBounds(X,y, WIDTH, HEIGHT);

        // 사용자 이름 라벨 세팅
        lblUserName = new JLabel("waiting");
        lblUserName.setForeground(Color.WHITE);
        lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
        lblUserName.setFont(new Font("굴림", Font.BOLD, 14));
        add(lblUserName);

        // 점수 라벨 세팅
        lblScore = new JLabel("0");
        lblScore.setVerticalAlignment(SwingConstants.TOP);
        lblScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblScore.setForeground(Color.WHITE);
        lblScore.setFont(new Font("굴림", Font.BOLD, 14));
        this.add(lblScore);

    }
    public void setUserName(String name) {
        lblUserName.setText(name);
    }

    public void setScore(String score) {
        lblScore.setText(score);
    }
}
