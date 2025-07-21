package org.example;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.ImageIcon;
import java.awt.*;
// ChatMsg.java 채팅 메시지 ObjectStream 용.
class ChatMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    public String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image, 500: Mouse Event
    public String UserName;
    public String data;
    public ImageIcon img;
    public MouseEvent mouse_e;
    public int pen_size; // pen size

    public Color color;//color

    public String shape;//도형
    public int x1, y1, x2, y2;// 좌표

    public String answer;// 정답
    public int order;//출제순서
    public int score;//점수
    public ChatMsg(String UserName, String code, String msg) {
        this.code = code;
        this.UserName = UserName;
        this.data = msg;
    }
}