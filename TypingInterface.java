import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

class TypingInterface extends JFrame implements ActionListener, KeyListener
{

    private JButton banglaButton, englishButton, endButton;
    private JLabel banglaInstructionLabel, englishInstructionLabel, timerLabel;
    private javax.swing.Timer countdownTimer;
    private int timeLeft;
    private JTextPane typingArea;
    private String referenceText = "";
    private int wordCount;
    private int correctChars, totalTypedChars;

    // Predefined passages for Bangla and English
    private final String[] banglaPassages =
    {
        "কিন্তু গাছের এত উপরে থাকা আঙুরের থোকা গুলির সে কীভাবে নাগাল পাবে সেই নিয়ে ভাবতে শুরু করল । কিন্তু শিয়ালটি তার কোনও উপায়ই বার করতে পারল না। অবশেষে বিফল মনোরথ হয়ে সে সেই স্থান পরিত্যাগ করল।",
        "ফড়িংটি প্রতিদিনই পিঁপড়েকে বিরতি নিতে বলত কিন্তু পিঁপড়ে নিজের কাজ চালিয়ে যেত। শীঘ্রই, শীত এল। রাতে ঠান্ডার প্রকোপ বেড়ে যাওয়ার ফলে খুব কম প্রাণী বাইরে বের হত। ফড়িংটি খাদ্যাভাবে সব সময় কষ্টে দিন যাপন করত ।",
        "অবশেষে এক বৃদ্ধ ইঁদুর দাঁড়িয়ে বলল, “আমি একটা খুব ভালো উপায়ের কথা চিন্তা করেছি ।আমরা যদি বিড়ালটির গলায় ঘণ্টা বেঁধে দি তাহলে বিড়ালটা যে আসছে সেই ঘণ্টার শব্দ শুনেই পরিষ্কারভাবে বোঝা যাবে।”"
    };

    private final String[] englishPassages =
    {
        "You're a little scary sometimes, you know that? Brilliant... but scary. Do not pity the dead, Harry. Pity the living, and above all, those who live without love.",
        "Driven by hunger, a fox tried to reach some grapes hanging high on the vine but was unable to, although he leaped with all his strength. As he went away, the fox remarked 'Oh, you aren't even ripe yet! I don't need any sour grapes.",
        "A group of mice agree to attach a bell to a cat's neck to warn of its approach in the future, but they fail to find a volunteer to perform the job."
    };

    public TypingInterface()
    {
        setTitle("SpeedTest");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel instructionPanel = new JPanel(new GridLayout(1, 2));

        // Bangla Instruction Label
        banglaInstructionLabel = new JLabel("<html><h3 style='text-align:center;'>Bangla Typing Instructions:</h3>"
                                            + "<ul style='text-align:left;'>"
                                            + "<li>Ensure your keyboard is set to Bangla layout.</li>"
                                            + "<li>Use Bangla characters as per your keyboard mapping.</li>"
                                            + "<li>If using an IME, activate it before typing.</li>"
                                            + "</ul></html>");
        banglaInstructionLabel.setVerticalAlignment(SwingConstants.TOP);

        // English Instruction Label
        englishInstructionLabel = new JLabel("<html><h3 style='text-align:center;'>English Typing Instructions:</h3>"
                                             + "<ul style='text-align:left;'>"
                                             + "<li>Use the standard QWERTY layout for typing.</li>"
                                             + "<li>Type directly in the text area for English.</li>"
                                             + "<li>Use punctuation keys for correct writing.</li>"
                                             + "</ul></html>");
        englishInstructionLabel.setVerticalAlignment(SwingConstants.TOP);

        instructionPanel.add(banglaInstructionLabel);
        instructionPanel.add(englishInstructionLabel);

        add(instructionPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        System.setProperty("file.encoding", "UTF-8");
        banglaButton = new JButton("Bangla Typing");
        englishButton = new JButton("English Typing");
        banglaButton.addActionListener(this);
        englishButton.addActionListener(this);
        buttonPanel.add(banglaButton);
        buttonPanel.add(englishButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);

        setVisible(true);
    }

    public void keyTyped(KeyEvent e)
    {
    }

    public void keyPressed(KeyEvent e)
    {
        if (countdownTimer == null)
        {
            startCountdown(120); // Start countdown for 2 minutes (120 seconds)
        }
        if (e.getKeyCode() == KeyEvent.VK_0)
        {
            System.exit(0); // Exit the application
        }
    }

    public void keyReleased(KeyEvent e)
    {
        if (typingArea.isEditable())
        {
            String typedText = typingArea.getText().trim();
            if (!typedText.isEmpty())
            {
                highlightText(typedText);
                updateTypingStatistics(typedText);
                if (typedText.equals(referenceText))
                {
                    endTypingTest(); // End the typing test if the text matches
                }
            }
        }
    }

    public void highlightText(String typedText)
    {
        try
        {
            StyledDocument doc = typingArea.getStyledDocument();
            removeHighlight();
            char[] referenceChars = referenceText.toCharArray();
            char[] typedChars = typedText.toCharArray();
            for (int i = 0; i < typedChars.length; i++)
            {
                Style style = doc.addStyle("Style", null);
                if (i < referenceChars.length && typedChars[i] == referenceChars[i])
                {
                    StyleConstants.setForeground(style, Color.GREEN);
                }
                else
                {
                    StyleConstants.setForeground(style, Color.RED);
                }
                doc.setCharacterAttributes(i, 1, style, false);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void removeHighlight()
    {
        try
        {
            StyledDocument doc = typingArea.getStyledDocument();
            Style normalStyle = doc.addStyle("NormalStyle", null);
            StyleConstants.setForeground(normalStyle, Color.BLACK);
            doc.setCharacterAttributes(0, doc.getLength(), normalStyle, false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void startCountdown(int seconds)
    {
        timeLeft = seconds;
        countdownTimer = new javax.swing.Timer(1000, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (timeLeft > 0)
                {
                    timeLeft--;
                    int minutes = timeLeft / 60;
                    int seconds = timeLeft % 60;
                    timerLabel.setText(String.format("Time Left: %d:%02d", minutes, seconds));
                }
                else
                {
                    countdownTimer.stop();
                    timerLabel.setText("Time's up!");
                    showTypingStatistics();
                    typingArea.setEditable(false);
                }
            }
        });
        countdownTimer.start();
    }

    private void showTypingStatistics()
    {
        double wpm = (wordCount / 2.0);
        double accuracy = (double) correctChars / totalTypedChars * 100;
        JOptionPane.showMessageDialog(TypingInterface.this,
                                      String.format("Your WPM: %.2f\nYour Accuracy: %.2f%%", wpm, accuracy));
    }

    private void updateTypingStatistics(String typedText)
    {
        String[] words = typedText.split("\\s+");
        wordCount = words.length;
        totalTypedChars = typedText.length();
        correctChars = 0;
        char[] referenceChars = referenceText.toCharArray();
        char[] typedChars = typedText.toCharArray();
        for (int i = 0; i < typedChars.length; i++)
        {
            if (i < referenceChars.length && typedChars[i] == referenceChars[i])
            {
                correctChars++;
            }
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == banglaButton)
        {
            dispose();
            createTypingFrame("Bangla Typing Mode", true);
        }
        else if (e.getSource() == englishButton)
        {
            dispose();
            createTypingFrame("English Typing Mode", false);
        }
    }

    private void createTypingFrame(String title, boolean isBangla)
    {
        JFrame typingFrame = new JFrame(title);
        typingFrame.setSize(600, 400);
        typingFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        typingFrame.setLayout(new BorderLayout());

        JTextPane nonEditableArea = new JTextPane();
        typingArea = new JTextPane();

        JPanel typingPanel = new JPanel(new BorderLayout());
        typingPanel.add(new JScrollPane(nonEditableArea), BorderLayout.CENTER);

        if (isBangla)
        {
            referenceText = getRandomPassage(banglaPassages);

            //  Bangla font
            try
            {
                Font kalpurushFont = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\spl_tst\\Kalpurush.ttf"));
                kalpurushFont  =  kalpurushFont .deriveFont(18f);
                nonEditableArea.setFont(kalpurushFont);
                typingArea.setFont(kalpurushFont);
            }
            catch (FontFormatException | IOException ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            referenceText = getRandomPassage(englishPassages);
            Font englishFont = new Font("C:\\spl_tst\\arialbi.ttf",Font.PLAIN, 18);
            nonEditableArea.setFont(englishFont);
            typingArea.setFont(englishFont);
        }

        nonEditableArea.setText(referenceText);
        nonEditableArea.setEditable(false);

        typingPanel.add(new JScrollPane(typingArea), BorderLayout.SOUTH);

        typingArea.addKeyListener(this);
        typingFrame.add(typingPanel, BorderLayout.CENTER);

        timerLabel = new JLabel("Time Left: 2:00");
        typingFrame.add(timerLabel, BorderLayout.NORTH);

        endButton = new JButton("End Test");
        endButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                endTypingTest();
            }
        });
        typingFrame.add(endButton, BorderLayout.SOUTH);

     
        typingFrame.setLocationRelativeTo(null);

        typingFrame.setVisible(true);
    }

    private String getRandomPassage(String[] passages)
    {
        Random random = new Random();
        return passages[random.nextInt(passages.length)];
    }

    private void endTypingTest()
    {
        if (countdownTimer != null)
        {
            countdownTimer.stop();
        }
        showTypingStatistics();
        typingArea.setEditable(false);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new TypingInterface();
            }
        });
    }
}
