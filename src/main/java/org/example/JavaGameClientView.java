package org.example;

// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창

import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.net.Socket;
import java.net.URL;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
public class JavaGameClientView extends JFrame {

    private static final long serialVersionUID = 1L;
    int MAX_USER_LEN = 4;
    private JPanel contentPane;

    private JTextField txtInput;
    private String UserName;
    private JButton btnSend;
    private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
    private Socket socket; // 연결소켓

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private JLabel lblAnswer,my_lblUserName;
    private JLabel lbltext;
    private JTextPane chatTextInputPane;
    //private int User_Count=0;
    private Frame frame;
    private FileDialog fd;
    private JButton imgBtn,fline,frec,rec,fcir,cir,line;
    Color b_color=Color.WHITE ;//버튼색
    Color b_bg_color=Color.DARK_GRAY;//버튼 바탕색 저장

    JPanel panel;
    private Graphics gc;
    private int pen_size = 2; // minimum 2
    // 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
    private Image panelImage = null;
    private Graphics gc2 = null;

    int x, y, ox, oy;         // 움직인 후의 좌표(x, y)와 움직이기 전의 좌표(ox, oy)
    Color colorVariable;//음?
    /*도형그리기 변수들*/
    int sx,sy,ex,ey;//사각형 그리기 startx,y, endx,y
    int xdif,ydif,xlen,ylen;
    private String shape="fd";//도형보내기용

    private Image tmpImage = null;
    private Graphics gc3 = null;//도형 중간과정

    private ArrayList<String> users = new ArrayList<>(); // 유저
    UserStatusPanel[] userStatus ={new UserStatusPanel(20), new UserStatusPanel(91),new UserStatusPanel(164),new UserStatusPanel(237)}; // user 상태창
    int[] score= {0,0,0,0};//username 점수 저장용
    private String answer;//정답
    private int currentOrder=0;//순서 넘기는 변수

    public JButton createHelperBtn(String name, Color color, int x,int y){ // 버튼 만드는 함수
        JButton customBtn = new JButton("");
        customBtn.setPreferredSize(new Dimension(50, 50)); // 버튼 크기 설정

        URL imageUrl = getClass().getResource("/images/" + name + ".png");

        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // 이미지 크기 조절
            customBtn.setIcon(new ImageIcon(scaledImage));
        } else {
            System.err.println("이미지를 찾을 수 없습니다: " + name);
        }

        customBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                colorVariable = color;
            }
        });
        customBtn.setBounds(x, y, 50, 50); // 위치 지정
        return customBtn;
    }

    // 클릭시 배경색 설정함수 (클릭효과 구현)
    private void setClickBackground(JButton selectedBtn) {
        JButton[] buttons = {frec, fline, rec,fcir, cir, line};
        for (JButton btn: buttons){
            if (btn == selectedBtn){
                btn.setBackground(b_bg_color);
            }else{
                btn.setBackground(b_color);
            }
        }
    }

    // 도형 버튼 생성 함수
    public JButton createShapeBtn(String name, String shapeValue, int x) {
        JButton customBtn = new JButton();
        customBtn.setPreferredSize(new Dimension(50, 50)); // 버튼 크기 설정
        URL imageUrl = getClass().getResource("/images/" + name + ".png");

        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // 이미지 크기 조절
            customBtn.setIcon(new ImageIcon(scaledImage));
        } else {
            System.err.println("이미지를 찾을 수 없습니다: " + name);
        }

        customBtn.setBounds(x, 551, 50, 50);
        customBtn.addActionListener(e -> {
            shape = shapeValue; //TODO 전역변수 설정
            setClickBackground(customBtn); // 클릭시 색 조정
        });
        return customBtn;
    }


    public JavaGameClientView(String username, String ip_addr, String port_no)  {
        setResizable(false); // 크기 조절 불가
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 닫기 버튼 클릭 시 프로그램 종료
        setBounds(100, 100, 750, 653);//Jframe크기

        // 메인 contentPane 설정
        contentPane = new JPanel();
        contentPane.setBackground(new Color(0, 102, 153));
        contentPane.setForeground(new Color(0, 0, 0));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);


        // 유저 상태창 붙이기
        for (UserStatusPanel statusPanel : userStatus){
            contentPane.add(statusPanel);
        }

        // 채팅 출력 창
        JScrollPane chatPane = new JScrollPane(); // 스크롤 가능한 영역 생성
        chatPane.setBounds(6, 310, 178, 174);
        contentPane.add(chatPane);

        chatTextInputPane = new JTextPane(); // 여러줄 텍스트 컴포넌트 생성
        chatTextInputPane.setBackground(new Color(255, 255, 204));
        chatPane.setViewportView(chatTextInputPane); // 스크롤 가능 영역 안에 텍스트 입력창 넣음
        chatTextInputPane.setEditable(false);
        chatTextInputPane.setFont(new Font("굴림체", Font.PLAIN, 14));

        // 채팅 입력 필드
        txtInput = new JTextField(); // 한줄 입력창
        txtInput.setBounds(331, 505, 301, 40);
        txtInput.setBackground(new Color(255, 255, 255));
        contentPane.add(txtInput);
        txtInput.setColumns(10);

        // 전송 버튼
        btnSend = new JButton("Send");
        btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
        btnSend.setBounds(644, 504, 69, 40);
        contentPane.add(btnSend);
        setVisible(true);

        // 접속 상태 출력 및 사용자 이름 저장
        AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
        UserName = username;

        // 이미지 보내기 버튼
        imgBtn = new JButton("밑그림");
        imgBtn.setFont(new Font("굴림", Font.PLAIN, 16));
        imgBtn.setBounds(559, 554, 151, 40);
        contentPane.add(imgBtn);

        // 종료 버튼
        JButton btnNewButton = new JButton("");
        btnNewButton.setIcon(new ImageIcon("images/exit.png"));
        btnNewButton.setFont(new Font("굴림", Font.PLAIN, 14));
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 종료 메시지 전송 후 종료
                ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
                SendObject(msg);
                System.exit(0);
            }
        });
        btnNewButton.setBounds(663, 10, 50, 40);
        contentPane.add(btnNewButton);
        //// 그림 그리는 패널
        panel = new JPanel();
        panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel.setBackground(Color.WHITE);
        panel.setBounds(187, 54, 526, 430);
        contentPane.add(panel);
        gc = panel.getGraphics(); // Graphics 객체 받아오기

        // Image 영역 보관용. paint() 에서 이용한다.
        createPaint(); // 그림 초기화


//      색상선택 버튼 create
        contentPane.add(createHelperBtn("btn_eraser",new Color(255,255,255), 270,505)); //TODO : color좀 선택적으로 쓸 수 없나?
        contentPane.add(createHelperBtn("btn_green",new Color(0,255,0),159,500));
        contentPane.add(createHelperBtn("btn_yellow",new Color(255,255,0),210,500));
        contentPane.add(createHelperBtn("btn_blue",new Color(0,0,255),100,500));
        contentPane.add(createHelperBtn("btn_red",new Color(255,0,0),57,500));
        contentPane.add(createHelperBtn("btn_black",new Color(0,0,0),6,500));


//        전체 지우기 버튼
        JButton rmv_btn_1 = new JButton("전체지우기");
        rmv_btn_1.setFont(new Font("굴림", Font.PLAIN, 16));
        rmv_btn_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeAll();
                ChatMsg obcm = new ChatMsg(UserName, "700", "eraseAll");
                SendObject(obcm);
            }
        });
        rmv_btn_1.setBounds(417, 554, 130, 40);
        contentPane.add(rmv_btn_1);

        // 도형 버튼들 set
        fline = createShapeBtn("btn_free_line","fd",6);
        contentPane.add(fline);

        frec = createShapeBtn("btn_rect","fillrect",57); //        사각형 그리기
        contentPane.add(frec);

        rec= createShapeBtn("btn_white_rec","drawrect",108);
        contentPane.add(rec);

        fcir = createShapeBtn("btn_circle","filloval",159);
        contentPane.add(fcir);

        cir = createShapeBtn("btn_white_circle","drawoval",210);
        contentPane.add(cir);

        line = createShapeBtn("btn_line","drawline",257);
        contentPane.add(line);


        lblAnswer = new JLabel("입장중....");
        lblAnswer.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnswer.setForeground(Color.WHITE);
        lblAnswer.setFont(new Font("굴림", Font.BOLD, 20));
        lblAnswer.setBorder(new LineBorder(new Color(0, 0, 0)));
        lblAnswer.setBackground(Color.WHITE);
        lblAnswer.setBounds(187, 10, 313, 40);
        contentPane.add(lblAnswer);

        my_lblUserName = new JLabel("name");
        my_lblUserName.setVerticalAlignment(SwingConstants.TOP);
        my_lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
        my_lblUserName.setForeground(Color.WHITE);
        my_lblUserName.setFont(new Font("굴림", Font.BOLD, 19));
        my_lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
        my_lblUserName.setBackground(Color.WHITE);
        my_lblUserName.setBounds(319, 555, 85, 49);
        contentPane.add(my_lblUserName);
        my_lblUserName.setText(username);

        lbltext = new JLabel("");
        lbltext.setHorizontalAlignment(SwingConstants.CENTER);
        lbltext.setForeground(Color.WHITE);
        lbltext.setFont(new Font("굴림", Font.BOLD, 20));
        lbltext.setBorder(new LineBorder(new Color(0, 0, 0)));
        lbltext.setBackground(Color.WHITE);
        lbltext.setBounds(512, 10, 140, 36);
        contentPane.add(lbltext);



        try {
            socket = new Socket(ip_addr, Integer.parseInt(port_no));

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();// 버퍼에 남아 있는 데이터를 바로 전송
            ois = new ObjectInputStream(socket.getInputStream());     // 서버로부터 데이터를 수신하기 위한 ObjectInputStream 생성


            ChatMsg obcm = new ChatMsg(UserName, "100", "Hello"); //누가 들어오면 server로 이걸 보낸다.
            SendObject(obcm);


            ListenNetwork net = new ListenNetwork();     // 서버로부터 들어오는 메시지를 수신할 스레드 시작
            net.start();

            // 텍스트 메시지 전송 이벤트를 처리할 액션 리스너 생성 및 연결
            TextSendAction action = new TextSendAction();
            btnSend.addActionListener(action); // TODO: 이걸 전역변수로 꼭 처리해야하나?
            txtInput.addActionListener(action);  // 입력창에서 Enter 키로 메시지 전송
            txtInput.requestFocus(); // 입력창 포커스

            // 이미지 전송 버튼에 대한 액션 리스너 설정
            ImageSendAction action2 = new ImageSendAction();
            imgBtn.addActionListener(action2);

            // 마우스 이벤트를 처리할 리스너 생성 및 등록 (그리기 기능 등)
            MyMouseEvent mouse = new MyMouseEvent();
            panel.addMouseMotionListener(mouse); //  // 마우스 이동 시 이벤트 처리
            panel.addMouseListener(mouse);  // 마우스 클릭 시 이벤트 처리

            // 마우스 휠 이벤트를 처리할 리스너 등록 (예: 브러시 크기 조절 등)
            MyMouseWheelEvent wheel = new MyMouseWheelEvent();
            panel.addMouseWheelListener(wheel);


        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
            AppendText("connect error"); // 화면에 오류 메시지 출력
        }

    }

    public void paint(Graphics g) {
        super.paint(g);
        // Image 영역이 가려졌다 다시 나타날 때 그려준다.
        //게임시작
        ChatMsg obcm = new ChatMsg(UserName, "150", "START");
        if(userStatus.length == MAX_USER_LEN) {
            //서버에 게임시작을 알림

            SendObject(obcm);

        }
        gc.drawImage(panelImage, 0, 0, this);
    }

    // Server Message를 수신해서 화면에 표시
    class ListenNetwork extends Thread {
        //  독립적으로 실행되는 백그라운드 작업 실행, 서버로부터 실시간으로 들어오는 메시지 처리
        //  서버로부터 들어오는 다양한 메시지를 비동기적으로 처리하기 위해 사용
        // 연결이 유지되는 동안 지속적으로 객체를 읽고 메시지를 분기 처리합니다.
        //  UI 스레드와 별개로 네트워크 수신 처리를 함으로써 UI 블로킹 없이 실시간 통신이 가능하도록 합니다.
        public void run() {
            while (true) {
                try {
                    Object obcm = null;
                    String msg = null;
                    ChatMsg chatMsg;
                    try {
                        obcm = ois.readObject(); //  서버로부터 직렬화된 객체를 읽어 옵니다
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                    if (obcm == null)
                        break;
                    if (obcm instanceof ChatMsg) {
                        chatMsg = (ChatMsg) obcm;
                        msg = String.format("[%s]\n%s", chatMsg.UserName, chatMsg.data);
                    } else
                        continue;
                    switch (chatMsg.code) {
                        case "100":
                            SetPlayer(chatMsg);
                            break;
                        case "150":
                            StartGame(chatMsg);
                            break;
                        case "200": // chat message
                            if (chatMsg.UserName.equals(UserName)) {
                                AppendTextR(msg); // 내 메세지는 우측에
                                ans_check(chatMsg);//답인지 체크
                            }
                            else
                                AppendText(msg);
                            break;
                        case "300": // Image 첨부
                            if (chatMsg.UserName.equals(UserName))
                                AppendImage(chatMsg.img);
                            break;
                        case "500": // Mouse Event 수신
                            DoMouseEvent(chatMsg);
                            break;
                        case "600": // 제시어 표시
                            SetWord(chatMsg);
                            break;
                        case "650": // 누군가 정답을 맞춤
                            handleCorrectAnswer(chatMsg);
                            break;

                        case "700": // erase all
                            removeAll();
                            break;

                    }
                } catch (IOException e) {
                    AppendText("ois.readObject() error");
                    try {
                        ois.close();
                        oos.close();
                        socket.close();

                        break;
                    } catch (Exception ee) {
                        break;
                    } // catch문 끝
                } // 바깥 catch문끝

            }
        }
    }

    // Mouse Event 수신 처리
    public void DoMouseEvent(ChatMsg cm) { // 네트워크로 수신한 마우스 이벤트 처리
        if (cm.UserName.matches(UserName)) // 본인 것은 이미 Local 로 그렸다.
            return;

        // 상대방이 설정한 색상으로 그리기 도구(gc2)의 색을 설정
        gc2.setColor(cm.color);

        //pensize 지정
        Graphics2D g2d = (Graphics2D)gc2;
        g2d.setStroke(new BasicStroke(cm.pen_size,BasicStroke.CAP_ROUND,0));


        int id = cm.mouse_e.getID();

        switch (id) {
            case MouseEvent.MOUSE_PRESSED -> {
                ox = cm.mouse_e.getX();
                oy = cm.mouse_e.getY();
                sx = cm.x1;
                sy = cm.y1;
            }

            case MouseEvent.MOUSE_DRAGGED -> {
                if ("fd".equals(cm.shape)) {
                    x = cm.mouse_e.getX();
                    y = cm.mouse_e.getY();
                    gc2.drawLine(ox, oy, x, y);
                    ox = x;
                    oy = y;
                }
            }

            case MouseEvent.MOUSE_RELEASED -> {
                ex = cm.x2;
                ey = cm.y2;
                xdif = ex - sx;
                ydif = ey - sy;
                xlen = Math.abs(xdif);
                ylen = Math.abs(ydif);

                // 좌표 계산
                int[] origin = getShapeOrigin(xdif, ydif, sx, sy, ex, ey);
                int drawX = origin[0], drawY = origin[1];

                switch (cm.shape) {
                    case "fillrect" -> gc2.fillRect(drawX, drawY, xlen, ylen);
                    case "drawrect" -> gc2.drawRect(drawX, drawY, xlen, ylen);
                    case "filloval" -> gc2.fillOval(drawX, drawY, xlen, ylen);
                    case "drawoval" -> gc2.drawOval(drawX, drawY, xlen, ylen);
                    case "drawline" -> gc2.drawLine(sx, sy, ex, ey);
                }
            }
        }

        // 백 버퍼에 그린 그림을 실제 패널에 출력
        gc.drawImage(panelImage, 0, 0, panel);
    }


    /**
     * 사각형/타원/선 그릴 때 방향에 따라 시작 좌표 계산
     */
    private int[] getShapeOrigin(int xdif, int ydif, int sx, int sy, int ex, int ey) {
        if (xdif >= 0 && ydif >= 0) return new int[]{sx, sy};     // 왼위 → 오아래
        if (xdif < 0 && ydif >= 0) return new int[]{ex, sy};      // 오위 → 왼아래
        if (xdif >= 0 && ydif < 0) return new int[]{sx, ey};      // 왼아래 → 오위
        else return new int[]{ex, ey};                            // 오아래 → 왼위
    }

    public void SendMouseEvent(MouseEvent e) {
        ChatMsg cm = new ChatMsg(UserName, "500", "MOUSE");
        cm.mouse_e = e;
        cm.pen_size = pen_size;
        cm.color= colorVariable;
        cm.shape=shape;
        cm.x1=sx;cm.y1=sy;cm.x2=ex;cm.y2=ey;
        SendObject(cm);
    }

    class MyMouseWheelEvent implements MouseWheelListener {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getWheelRotation() < 0) { // 위로 올리는 경우 pen_size 증가
                if (pen_size < 20)
                    pen_size++;
            } else {
                if (pen_size > 2)
                    pen_size--;
            }

        }

    }
    // Mouse Event Handler (로컬 마우스 이벤트 처리)
    class MyMouseEvent implements MouseListener, MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent e) {
            // 색상과 펜 굵기 설정
            gc2.setColor(colorVariable);
            Graphics2D g2d = (Graphics2D) gc2;
            g2d.setStroke(new BasicStroke(pen_size, BasicStroke.CAP_ROUND, 0));

            // 현재 마우스 좌표 저장
            x = e.getX();
            y = e.getY();

            // 자유 그리기(free draw) 모드인 경우: 이전 좌표에서 현재 좌표까지 선을 그림
            if (shape.equals("fd")) {
                gc2.drawLine(ox, oy, x, y);
            }

            // 이전 좌표 갱신
            ox = x;
            oy = y;
            ex = x;
            ey = y;

            // 시작 좌표와 현재 좌표 차이 계산
            xdif = ex - sx;
            ydif = ey - sy;
            xlen = Math.abs(xdif);
            ylen = Math.abs(ydif);

            // 자유 그리기가 아닐 경우, tmpImage를 현재 화면에 그려서 잔상 제거
            if (!shape.equals("fd")) {
                gc.drawImage(tmpImage, 0, 0, panel);
                gc2.drawImage(tmpImage, 0, 0, panel);
            }

            // 도형 종류에 따라 그리는 동작 분기
            // 도형 미리보기만 구현, 확정적으로 도형 그리는것은 MouseRelease Event에서 처리. (화면 깜빡이는 문제가 있음)
            switch (shape) {
                case "fillrect": // 채운 사각형
                    if (ydif > 0 && xdif < 0)
                        gc2.fillRect(ex, sy, xlen, ylen); // 오른쪽 위 → 왼쪽 아래
                    else if (xdif > 0 && ydif > 0)
                        gc2.fillRect(sx, sy, xlen, ylen); // 왼쪽 위 → 오른쪽 아래
                    else if (xdif < 0 && ydif < 0)
                        gc2.fillRect(ex, ey, xlen, ylen); // 오른쪽 아래 → 왼쪽 위
                    else if (xdif > 0 && ydif < 0)
                        gc2.fillRect(sx, ey, xlen, ylen); // 왼쪽 아래 → 오른쪽 위
                    break;

                case "drawrect": // 선만 있는 사각형
                    if (ydif > 0 && xdif < 0)
                        gc2.drawRect(ex, sy, xlen, ylen);
                    else if (xdif > 0 && ydif > 0)
                        gc2.drawRect(sx, sy, xlen, ylen);
                    else if (xdif < 0 && ydif < 0)
                        gc2.drawRect(ex, ey, xlen, ylen);
                    else if (xdif > 0 && ydif < 0)
                        gc2.drawRect(sx, ey, xlen, ylen);
                    break;

                case "filloval": // 채운 타원
                    if (ydif > 0 && xdif < 0)
                        gc2.fillOval(ex, sy, xlen, ylen);
                    else if (xdif > 0 && ydif > 0)
                        gc2.fillOval(sx, sy, xlen, ylen);
                    else if (xdif < 0 && ydif < 0)
                        gc2.fillOval(ex, ey, xlen, ylen);
                    else if (xdif > 0 && ydif < 0)
                        gc2.fillOval(sx, ey, xlen, ylen);
                    break;

                case "drawoval": // 선만 있는 타원
                    if (ydif > 0 && xdif < 0)
                        gc2.drawOval(ex, sy, xlen, ylen);
                    else if (xdif > 0 && ydif > 0)
                        gc2.drawOval(sx, sy, xlen, ylen);
                    else if (xdif < 0 && ydif < 0)
                        gc2.drawOval(ex, ey, xlen, ylen);
                    else if (xdif > 0 && ydif < 0)
                        gc2.drawOval(sx, ey, xlen, ylen);
                    break;

                case "drawline": // 직선 그리기
                    gc2.drawLine(sx, sy, ex, ey);
                    break;

                default:
                    // 정의되지 않은 도형 형태인 경우 아무 동작 안 함
                    break;
            }

            // 최종 결과 이미지를 패널에 그림 (paint()용 panelImage 반영)
            gc.drawImage(panelImage, 0, 0, panel);

            // 다른 사용자에게 현재 마우스 위치 전송
            SendMouseEvent(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {}

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            gc3.drawImage(panelImage,0,0,panel);
            ox=e.getX(); oy=e.getY();
            sx=e.getX(); sy=e.getY();//사각형 시작좌표, 원 시작좌표
            SendMouseEvent(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) { // 여기서 확정적으로 도형을 그려야 깜빡임이 없게 처리됨
            ex=e.getX(); ey=e.getY(); // 사각형 끝나는좌표

            if(shape.equals("fillrect")) {// 사각형
                gc2.fillRect(sx,sy,ex-sx,ey-sy);
            }
            repaint();
            SendMouseEvent(e);
            // TODO: 나머지 구현 필요
        }
    }

    // keyboard enter key 치면 서버로 전송
    class TextSendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Send button을 누르거나 메시지 입력하고 Enter key 치면
            if (e.getSource() == btnSend || e.getSource() == txtInput) {
                String msg = null;
                msg = txtInput.getText();
                SendMessage(msg);
                txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
                txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
                if (msg.contains("/exit")) // 종료 처리
                    System.exit(0);
            }
        }
    }

    class ImageSendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
            if (e.getSource() == imgBtn) {
                frame = new Frame("이미지첨부");
                fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
                fd.setVisible(true);
                if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
                    ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");
                    ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
                    obcm.img = img;
                    SendObject(obcm);
                }
            }
        }
    }
    public void removeAll() {
        tmpImage=null;
        panelImage = null;
        panel.repaint();
        createPaint();
    }
    
    public void createPaint() {
        panelImage = createImage(panel.getWidth(), panel.getHeight());
        gc2 = panelImage.getGraphics();
        gc2.setColor(panel.getBackground());
        gc2.fillRect(0,0, panel.getWidth(),  panel.getHeight());
        gc2.setColor(Color.BLACK);
        gc2.drawRect(0,0, panel.getWidth()-1,  panel.getHeight()-1);


        tmpImage = createImage(panel.getWidth(), panel.getHeight());
        gc3 = tmpImage.getGraphics();
        gc3.setColor(panel.getBackground());
        gc3.fillRect(0,0, panel.getWidth(),  panel.getHeight());
        gc3.setColor(Color.BLACK);
        gc3.drawRect(0,0, panel.getWidth()-1,  panel.getHeight()-1);
    }
    public void handleCorrectAnswer(ChatMsg cm){ // 정답을 맞춘경우
        lbltext.setText(cm.UserName+"님 정답!"); // 맞은사람 이름표시
        if(cm.UserName.equals(users.get(0))) {
            userStatus[0].setScore(String.valueOf(cm.score));
        }
        if(cm.UserName.equals(users.get(1))) {
            userStatus[1].setScore(String.valueOf(cm.score));
        }
        if(cm.UserName.equals(users.get(2))) {
            userStatus[2].setScore(String.valueOf(cm.score));
        }
        if(cm.UserName.equals(users.get(3))) {
            userStatus[3].setScore(String.valueOf(cm.score));
        }
    }

    public void SetPlayer(ChatMsg cm) {
        if(users.size() == MAX_USER_LEN) { // 인원이 다 차면
            ChatMsg obcm = new ChatMsg(UserName, "150", "START"); // 서버에 게임 시작을 알림
            SendObject(obcm);
        }else{
            userStatus[users.size()].setUserName(cm.UserName);
            users.add(cm.UserName);
        }
    }

    public void StartGame(ChatMsg cm) {
        //TODO: 게임 시작되면 할 일
    }

    public void SetWord(ChatMsg cm) {
        answer = cm.answer; // 정답 set
        currentOrder = cm.order; // 출제순서 set
        currentOrder = currentOrder % MAX_USER_LEN;

        if(cm.UserName.equals(users.get(currentOrder))){ // username이 array[currentOrder]랑 같으면
            lblAnswer.setText(answer);
        }
        else {
            lblAnswer.setText("답을 맞춰보세요");
        }
    }
    public void ans_check(ChatMsg cm) {
        if(answer.equals(cm.data)) {//답이 맞으면
            ChatMsg obcm = new ChatMsg(UserName, "650", "correct");//맞았다고 서버에 알리기
            for(int i = 0; i< users.size(); i++) {
                if(users.get(i).matches(UserName)) {//맞춘사람 score를 100 올려주고
                    score[i]+=100;
                    obcm.score=score[i];	//chatmsg에 저장하여 서버로 보낸다.
                }
            }

            SendObject(obcm);
            removeAll();
            ChatMsg rmcm = new ChatMsg(UserName, "700", "eraseAll");//화면모두지우기
            SendObject(rmcm);

        }
    }

    // 화면에 출력
    public void AppendText(String msg) {

        msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
        StyledDocument doc = chatTextInputPane.getStyledDocument();
        SimpleAttributeSet left = new SimpleAttributeSet();
        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(left, Color.BLACK);
        doc.setParagraphAttributes(doc.getLength(), 1, left, false);
        try {
            doc.insertString(doc.getLength(), msg+"\n", left );
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        int len = chatTextInputPane.getDocument().getLength();
        chatTextInputPane.setCaretPosition(len);
    }

    // 화면 우측에 출력
    public void AppendTextR(String msg) {
        msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
        StyledDocument doc = chatTextInputPane.getStyledDocument();
        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(right, Color.BLUE);
        doc.setParagraphAttributes(doc.getLength(), 1, right, false);
        try {
            doc.insertString(doc.getLength(),msg+"\n", right );
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        int len = chatTextInputPane.getDocument().getLength();
        chatTextInputPane.setCaretPosition(len);
    }

    public void AppendImage(ImageIcon ori_icon) {

        Image ori_img = ori_icon.getImage();
        gc2.drawImage(ori_img,  0,  0, panel.getWidth(), panel.getHeight(), panel);
        gc.drawImage(panelImage, 0, 0, panel.getWidth(), panel.getHeight(), panel);
    }


    // Server에게 network으로 전송
    public void SendMessage(String msg) {
        try {

            ChatMsg obcm = new ChatMsg(UserName, "200", msg);
            oos.writeObject(obcm);
        } catch (IOException e) {
            // AppendText("dos.write() error");
            AppendText("oos.writeObject() error");
            try {
                ois.close();
                oos.close();
                socket.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(0);
            }
        }
    }

    public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
        try {
            oos.writeObject(ob);
        } catch (IOException e) {
            AppendText("SendObject Error");
        }
    }
}
