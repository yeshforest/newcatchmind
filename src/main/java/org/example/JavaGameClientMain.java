package org.example;

// JavaObjClient.java
// 사용자가 클라이언트에 접속할때 필요한 정보를 입력받는 접속창(로그인창)역할
// ObjecStream 사용하는 채팅 Client

import java.awt.event.*;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class JavaGameClientMain extends JFrame { // JFrame은 Java Swing에서 기본적인 윈도우 창을 만들어주는 클래스입니다
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtUserName;
    private JTextField txtIpAddress;
    private JTextField txtPortNumber;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() { // 이벤트 큐에 Runnable을 넣어 UI 실행
            public void run() {
                try {
                    JavaGameClientMain frame = new JavaGameClientMain();
                    frame.setVisible(true); // 화면에 보여주기
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public JavaGameClientMain() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫기 버튼 클릭 시 프로그램 종료
        setBounds(100, 100, 254, 321); // 창 위치(x=100, y=100)와 크기(가로254, 세로321) 설정
        contentPane = new JPanel(); // 기본 컨테이너로 사용할 JPanel 생성
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); // 패널 테두리에 5픽셀씩 빈 여백 설정
        setContentPane(contentPane); // JFrame에 contentPane을 메인 컨테이너로 설정
        contentPane.setLayout(null); // 레이아웃 매니저 사용 안함(좌표 직접 지정)

        JLabel lblNewLabel = new JLabel("User Name"); // 사용자 이름 입력 안내 라벨 생성
        lblNewLabel.setBounds(12, 39, 82, 33); // 위치 및 크기 설정 (x=12, y=39, 너비=82, 높이=33)
        contentPane.add(lblNewLabel); // contentPane에 라벨 추가

        txtUserName = new JTextField(); // 사용자 이름 입력 필드 생성
        txtUserName.setHorizontalAlignment(SwingConstants.CENTER); // 텍스트 중앙 정렬
        txtUserName.setBounds(101, 39, 116, 33); // 위치 및 크기 설정
        contentPane.add(txtUserName); // contentPane에 텍스트 필드 추가
        txtUserName.setColumns(10); // 필드 너비 기준 열 수 설정 (입력 가능한 문자 길이 기준)

        JLabel lblIpAddress = new JLabel("IP Address"); // IP 주소 입력 안내 라벨 생성
        lblIpAddress.setBounds(12, 100, 82, 33); // 위치 및 크기 설정
        contentPane.add(lblIpAddress); // contentPane에 라벨 추가

        txtIpAddress = new JTextField(); // IP 주소 입력 필드 생성
        txtIpAddress.setHorizontalAlignment(SwingConstants.CENTER); // 텍스트 중앙 정렬
        txtIpAddress.setText("127.0.0.1"); // 기본값을 로컬호스트 주소로 설정
        txtIpAddress.setColumns(10); // 필드 너비 기준 열 수 설정
        txtIpAddress.setBounds(101, 100, 116, 33); // 위치 및 크기 설정
        contentPane.add(txtIpAddress); // contentPane에 텍스트 필드 추가

        JLabel lblPortNumber = new JLabel("Port Number"); // 포트 번호 입력 안내 라벨 생성
        lblPortNumber.setBounds(12, 163, 82, 33); // 위치 및 크기 설정
        contentPane.add(lblPortNumber); // contentPane에 라벨 추가

        txtPortNumber = new JTextField(); // 포트 번호 입력 필드 생성
        txtPortNumber.setText("30000"); // 기본값으로 30000 포트 설정
        txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER); // 텍스트 중앙 정렬
        txtPortNumber.setColumns(10); // 필드 너비 기준 열 수 설정
        txtPortNumber.setBounds(101, 163, 116, 33); // 위치 및 크기 설정
        contentPane.add(txtPortNumber); // contentPane에 텍스트 필드 추가

        JButton btnConnect = new JButton("Connect"); // 접속 시도 버튼 생성
        btnConnect.setBounds(12, 223, 205, 38); // 버튼 위치 및 크기 설정 (넓게 배치)
        contentPane.add(btnConnect); // contentPane에 버튼 추가

        Myaction action = new Myaction(); // 버튼 및 텍스트 필드 이벤트 처리용 리스너 생성
        btnConnect.addActionListener(action); // Connect 버튼 클릭 시 액션 리스너 등록
        txtUserName.addActionListener(action); // 사용자 이름 입력 후 엔터 시 액션 리스너 등록
        txtIpAddress.addActionListener(action); // IP 입력 후 엔터 시 액션 리스너 등록
        txtPortNumber.addActionListener(action); // 포트 입력 후 엔터 시 액션 리스너 등록
    }

    class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
        // 내부클래스로 처리한 이유
        //  1) 다른 클래스나 다른 화면에서 재사용할 일이 없다면, 굳이 바깥으로 빼서 public class로 만들 필요가 없습니다.
        //  2) txtUserName등 GUI 컴포넌트에 직접 접근하고있음. 외부 클래스의 컴포넌트에 쉽게 접근하기 위해

    {
        @Override
        public void actionPerformed(ActionEvent e) { // 이벤트 발생 시 호출되는 메서드
            String username = txtUserName.getText().trim(); // 사용자 이름 입력값 가져와 공백 제거
            String ip_addr = txtIpAddress.getText().trim();  // IP 주소 입력값 가져와 공백 제거
            String port_no = txtPortNumber.getText().trim();  // 포트 번호 입력값 가져와 공백 제거

            // 위 입력값을 기반으로 새로운 클라이언트 뷰(채팅 화면 등)를 생성
            JavaGameClientView view = new JavaGameClientView(username, ip_addr, port_no);
            setVisible(false); // 현재 로그인 창 숨기기 (새 창으로 넘어감)
        }
    }
}