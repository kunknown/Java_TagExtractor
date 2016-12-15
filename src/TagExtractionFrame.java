

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author brunnese
 */
public class TagExtractionFrame extends JFrame
{
    JPanel main = new JPanel();
    BorderLayout bordLayout = new BorderLayout();
    
    String outFileName = "";
    
    Scanner console = new Scanner(System.in);
    
    File classFile;
    Scanner inFile;

    FileFilter filter = new FileNameExtensionFilter("Class Set File", "txt", "text", "csv");

    JFileChooser chooser = new JFileChooser();
    
    ArrayList<String> stopWords = new ArrayList<>();
    
    File stopWordsFile = new File("StopWords.txt");
    
    JTextArea textArea = new JTextArea(50, 50);

    JScrollPane scroller = new JScrollPane(textArea);

    JPanel north = new JPanel();
    JPanel south = new JPanel();
    JPanel inputPane = new JPanel();
    JPanel outputPane = new JPanel();
    
    JButton open = new JButton("OPEN A TEXT FILE");
    JButton quit = new JButton("QUIT");
    JButton saveStopWord = new JButton("SAVE STOP-WORDS TO TEXT & UPDATE LIST");
    JButton saveTagCount = new JButton("SAVE LIST TO TEXT");
    
    JLabel inputLabel = new JLabel("# of tags to add in the stop-word file [MAX 5]:");
    JLabel outputLabel = new JLabel("Enter the name of the text file (Don't type .txt): ");
    
    JTextField inputField = new JTextField(10);
    JTextField outputField = new JTextField(15);
    
    ActionListener quitListener, openListener, saveStopListener, saveListListener;
    
    Map<String, Integer> frequencies = new TreeMap<String, Integer>();
    
    PrintWriter outputFile;
   
    public TagExtractionFrame()
    {
        super("Tag Extractor");

        //set layout of bord
        main.setLayout(bordLayout);
        south.setLayout(new BorderLayout());
        
        north.add(open);
        north.add(quit);
        
        inputPane.add(inputLabel);
        inputPane.add(inputField);
        inputPane.add(saveStopWord);
        
        outputPane.add(outputLabel);
        outputPane.add(outputField);
        outputPane.add(saveTagCount);
        
        south.add(inputPane, BorderLayout.NORTH);
        south.add(outputPane, BorderLayout.SOUTH);
        
        main.add(north, BorderLayout.NORTH);
        main.add(scroller, BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);
        
        // add the panel to the frame
        add(main);
        
        
        createButton();
        
        
        // configure the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        
       
    }
    
   
    
    public String clean(String s)
    {
       String r = "";
       for (int i = 0; i < s.length(); i++)
       {
          char c = s.charAt(i);
          if (Character.isLetter(c))
          {
             r = r + c;
          }
       }
       return r.toLowerCase();
    }
    
    private void outputList()
    {
        String output = "";
        for (String key : frequencies.keySet())
            {
                //System.out.println(key);
                boolean test = stopWords.contains(key);
                //System.out.println(test);
                if(!test)
                {
                    output = String.format("%-20s%10d\n", key, frequencies.get(key));
                    textArea.append(output);
                }
            }
    }
    private void stopWords()
    {
        try
        {   
            Scanner in = new Scanner(stopWordsFile);
            int counter = 0;
            while (in.hasNext())
            {
               String word = clean(in.next()); 

               stopWords.add(counter, word);

               counter++;
               //System.out.println(stopWords);
            }
 
        }
        
        catch(FileNotFoundException ex)
        {
            System.out.println("Error, could not create output file!");
            System.exit(0);
        } 
        
        catch (IOException ex)
        {
            ex.getStackTrace();
            System.out.println("IO ERROR trying to read file!");
            return;
        }
    }
    
    private void createButton()
    {
        quit.addActionListener(quitListener);
        quit.addActionListener((ActionEvent ae) ->
        {
            System.exit(0);
        });
        
        open.addActionListener(openListener);
        open.addActionListener((ActionEvent ae) ->
        {
            chooser.addChoosableFileFilter(filter);
            File workingDirectory = new File(System.getProperty("user.dir"));
            chooser.setCurrentDirectory(workingDirectory);

            try
            {
                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    classFile = chooser.getSelectedFile();

                    Scanner in = new Scanner(classFile);
                    while (in.hasNext())
                    {
                        String word = clean(in.next());    

                        // Get the old frequency count

                        Integer count = frequencies.get(word);
                        // If there was none, put 1; otherwise, increment the count
                        if (count == null) 
                        { 
                            count = 1; 
                        }
                        else 
                        { 
                            count = count + 1; 
                        }        
                        frequencies.put(word, count);                                               
                    }   
                        outputList();
                        open.setEnabled(false);
                }
            else 
                {
                    System.out.println("You must choose a file.");
                    System.exit(0);

                }
            }
            catch(FileNotFoundException ex)
            {
                System.out.println("Error, could not create output file!");
                System.exit(0);
            } 

            catch (IOException ex)
            {
                ex.getStackTrace();
                System.out.println("IO ERROR trying to read file!");
                return;
            }
        });
        
        saveStopWord.addActionListener(saveStopListener);
        saveStopWord.addActionListener((ActionEvent ae) ->
        {
            saveStopWord.setEnabled(false);
            //SORTING THE MAP using its values
            SortedSet<Integer> values = new TreeSet<Integer>(frequencies.values());
            System.out.println(values);
            //System.out.println(values.size());
            int intInput = Integer.parseInt(inputField.getText());
            //System.out.println(intInput);
            //System.out.println(values.size() - intInput);
            try
            {
                PrintWriter stopWords;
                stopWords = new PrintWriter("StopWords.txt");
                for(int temp = values.size() - intInput; temp <= values.size(); temp++)
                {
                    //System.out.println(frequencies.get("a"));
                    //System.out.println(temp);

                    for (String key : frequencies.keySet())
                    {
                        int tempValue = values.last();
                        if(frequencies.get(key) == tempValue)
                            {
                                //System.out.println(frequencies.get(key));
                                String output = String.format("%-20s%10d", key, frequencies.get(key));
                                System.out.println(output);
                                values.remove(values.last());
                                //System.out.println(values);
                                stopWords.println(key);
                            }
                    }
                }stopWords.close();
            }
            catch(FileNotFoundException ex)
            {
                System.out.println("Could not open file for writing.\n");
                System.exit(0);
            }
            
            //UPDATE TEXT AREA
            stopWords();
            textArea.setText("");
            outputList();
            
        });
        saveTagCount.addActionListener(saveListListener);
        saveTagCount.addActionListener((ActionEvent ae) ->
        {
           saveTagCount.setEnabled(false);
           try
            {
                String outputFileName = outputField.getText();
                outputFile = new PrintWriter(outputFileName + ".txt");
                for (String key : frequencies.keySet())
            {
                //System.out.println(key);
                boolean test = stopWords.contains(key);
                //System.out.println(test);
                if(!test)
                {
                    String output = String.format("%-20s%10d\n", key, frequencies.get(key));
                    outputFile.println(output);
                }
            }
                outputFile.close();
            }
            catch(FileNotFoundException ex)
            {
                System.exit(0);
            }
        });
        
        
         
    }
    
    
}
