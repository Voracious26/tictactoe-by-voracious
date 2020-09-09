package tictactoe;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class TicTacToe {
    public static int turn = 0;
    public static int boardSize = 3;
    public static int winSize = 3;
    public static int imgSize = 150;
    public static boolean gameOver = false;
    public static JTextArea infoArea;
    public static String welcomeMsg = "It's Tic Tac Toe time. Smexy Sanic vs Smexier Sanic, who will win? Click on a square to make your turn when you're ready.";
        
    public static void main(String args[]){
        
        JFrame f = new JFrame();
        f.setSize(imgSize*boardSize,imgSize*boardSize+20);
        f.setTitle("Tic-Tac-Toe - by Voracious Fall 2020");
        f.setLayout(new BorderLayout());        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // info pane
        JPanel info = new JPanel();
        infoArea = new JTextArea (1, 30);
        JButton playAgain = new JButton("Play Again");
        playAgain.setVisible(false);
        
        
        infoArea.setText(welcomeMsg);
        infoArea.setWrapStyleWord(true);
        infoArea.setLineWrap(true);
        infoArea.setOpaque(false);
        infoArea.setEditable(false);
        infoArea.setFocusable(false);
        info.add(infoArea);
        info.add(playAgain);
        
        // board pane
        JPanel board = new JPanel();
        board.setLayout(new GridLayout(boardSize,boardSize));
        JButton[] buttons = new JButton[boardSize * boardSize]; 
        ImageIcon xImg = scaleIcon("x.png", imgSize, imgSize);
        ImageIcon oImg = scaleIcon("o.png", imgSize, imgSize);
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            buttons[i].putClientProperty("INDEX", i);
            buttons[i].putClientProperty("VALUE", ' ');
            buttons[i].addActionListener((ActionEvent e) -> {
                JButton b = (JButton)e.getSource();
                char state = processClick(b, buttons, xImg, oImg);
                if(state == 'F' || state == 'X' || state == 'O'){
                    String paneMsg = "";
                    if(state == 'F')
                        paneMsg = "It's a tie. How unexpected.";
                    else if(state == 'X')
                        paneMsg = "You won the game! Congratulations.";
                    else if(state == 'O')
                        paneMsg = "You lost. May you never forget it.";
                    infoArea.setText(paneMsg);
                    playAgain.setVisible(true);
                }
                
                turn++;
            });
            board.add(buttons[i]);
        }
        
        playAgain.addActionListener((ActionEvent e) -> {
                playAgain(buttons);                
                JButton b = (JButton)e.getSource();
                b.setVisible(false);
        });
        
        f.add(board, BorderLayout.CENTER);
        f.add(info, BorderLayout.SOUTH);        
        f.setVisible(true);
    }
    
    public static ImageIcon scaleIcon(String url, int width, int height){
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image dimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);        
        return new ImageIcon(dimg);       
    }
    public static char processClick(JButton button, JButton[] buttons, ImageIcon xImg, ImageIcon oImg){       
        char value = (char) button.getClientProperty("VALUE");
        if(value == ' ' && !gameOver){
            button.putClientProperty("VALUE", 'X');
            button.setIcon(xImg);
        }
        else{
            // i.e. you clicked on a filled space
            return 'x';
        }
        char state = checkWin(buttons);
        if(state == 'F' || state == 'X' || state == 'O'){
            gameOver = true;
            return state;
        }
        
        computerMove(buttons, oImg);
        state = checkWin(buttons);
        if(state == 'F' || state == 'X' || state == 'O')
            gameOver = true;
        
        return state;
        
    }
    public static void computerMove(JButton[] buttons, ImageIcon oImg){
        int rNum = 0;
        while((char) buttons[rNum].getClientProperty("VALUE") != ' '){
            rNum = ThreadLocalRandom.current().nextInt(0, buttons.length);
        }
        buttons[rNum].setIcon(oImg);
        buttons[rNum].putClientProperty("VALUE", 'O');
    }
    public static char checkWin(JButton[] buttons){
        char winner = ' ';
        
        /// make a 2d char array containing the values in the board
        char[][] arr = new char[boardSize][boardSize];
        for(int i = 0; i < boardSize; i++)
            for(int j = 0; j < boardSize; j++)
                arr[i][j] = ' ';        
        
        for(JButton button : buttons){
            int index = (int) button.getClientProperty("INDEX");
            char value = (char) button.getClientProperty("VALUE"); 
            arr[index/boardSize][index%boardSize] = value;               
        }
        
        char prev;
        int soFar;
        
        // horizontal
        for (int i = 0; i < arr.length; i++){
            soFar = 0;
            prev = ' ';      
            for (int j = 0; j < arr[0].length; j++){
                soFar++;
                if (j != 0 && prev != arr[i][j])
                    soFar = 0;
                if (arr[i][j] == ' ')
                    soFar = 0;
                if (soFar >= winSize)
                    winner = arr[i][j];                
                prev = arr[i][j];
            }
        }  
        
        // vertical
        for (int j = 0; j < arr[0].length; j++){
            soFar = 0;
            prev = ' ';      
            for (int i = 0; i < arr.length; i++){
                soFar++;
                if (i != 0 && prev != arr[i][j])
                    soFar = 0;
                if (arr[i][j] == ' ')
                    soFar = 0;
                if (soFar >= winSize)
                    winner = arr[i][j];                
                prev = arr[i][j];
            }
        }
        
        // down right diagonal
        for (int i = 0; i < arr.length - winSize + 1; i++){
            for (int j = 0; j < arr[0].length - winSize + 1; j++){
                if(!(arr[i][j] == ' ')){
                    boolean match = true;
                    prev = arr[i][j];
                    for (int k = 0; k < winSize; k++){                        
                        if(arr[i+k][j+k] != prev)
                            match = false;
                    }
                    if (match)
                        winner = prev;
                }
            }
        }
        
        // down left diagonal
        for (int i = 0; i < arr.length - winSize + 1; i++){
            for (int j = winSize-1; j < arr[0].length; j++){
                if(!(arr[i][j] == ' ')){
                    boolean match = true;
                    prev = arr[i][j];
                    for (int k = 0; k < winSize; k++){                        
                        if(arr[i+k][j-k] != prev)
                            match = false;
                    }
                    if (match)
                        winner = prev;
                }
            }
        }
        
        boolean full = true;
        for(JButton button : buttons){
            char value = (char) button.getClientProperty("VALUE");             
            // check if there are any empty spaces in the board, if not it's a tie game 
            if(value == ' ')
                full = false;
        }
        
        if(full && winner == ' ')
            winner = 'F';       
        
        return winner;        
    }
    public static void playAgain(JButton[] buttons){
        for(JButton button : buttons){
            button.putClientProperty("VALUE", ' ');
            button.setIcon(new ImageIcon());            
        }
        gameOver = false;
        infoArea.setText(welcomeMsg);
    }
}


