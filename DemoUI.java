import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class DemoUI extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private String queryType = "NGram";
	JTextArea result = new JTextArea();
	JTextField queryInput = new JTextField();
	JTextField matchRate = new JTextField();

	public static void main(String[] args)
	{
		new DemoUI();
	}
	
	public DemoUI()
	{
		super("NGramQuery Demo");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		String ngram = "NGramQuery";
		JRadioButton ngramQuery = new JRadioButton(ngram);
		ngramQuery.setActionCommand(ngram);
		ngramQuery.addActionListener(this);
		ngramQuery.setSelected(true);
		
		String term = "TermQuery";
		JRadioButton termQuery = new JRadioButton(term);
		termQuery.setActionCommand(term);
		termQuery.addActionListener(this);

		String largeIndex = "BigIndex";
		JRadioButton largeQuery = new JRadioButton(largeIndex);
		largeQuery.setActionCommand(largeIndex);
		largeQuery.addActionListener(this);
		
		ButtonGroup radioButtons = new ButtonGroup();
		radioButtons.add(ngramQuery);
		radioButtons.add(termQuery);
		radioButtons.add(largeQuery);
		
		JPanel radioPanel = new JPanel(new GridLayout(1,0));
		radioPanel.add(ngramQuery);
		radioPanel.add(termQuery);
		radioPanel.add(largeQuery);
		
		
		JButton search = new JButton("Query text");
		search.addActionListener(this);
		
		JPanel optionPanel = new JPanel(new GridLayout(0,1));
		optionPanel.add(queryInput);
		optionPanel.add(matchRate);
		optionPanel.add(radioPanel);
		optionPanel.add(search);
		optionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.add(optionPanel, BorderLayout.WEST);
		
		result.setSize(50, 50);
		result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JScrollPane scrbar = new JScrollPane(result);
		scrbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrbar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(scrbar, BorderLayout.CENTER);
		
		this.setSize(1000, 350);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getActionCommand() == "NGramQuery")
			queryType = "NGram";
		else if(event.getActionCommand() == "TermQuery")
			queryType = "Term";
		else if(event.getActionCommand() == "BigIndex")
			queryType = "BigIndex";
	
		else if(event.getActionCommand() == "Query text")
		{

			String input = queryInput.getText();
			String rateTxt = matchRate.getText();
			double rate =0.5;
			if(!rateTxt.equals(""))
				rate = Double.parseDouble(rateTxt);
			Query query;
			try {
				IndexReader reader;
				if(queryType.equals("NGram"))
				{
					query = NGramQueryParser.parse(input, rate);
			    	reader = IndexReader.open(FSDirectory.open(new File("D:\\result2")));
					result.setText("NGram query result:\n");
					
				}
				else if(queryType.equals("BigIndex"))
				{
					query = NGramQueryParser.parse(input, rate);
			    	reader = IndexReader.open(FSDirectory.open(new File("D:\\result")));
					result.setText("NGram query result:\n");
					
				}
				else
				{
					query = new TermQuery(new Term("contents", input));
					reader = IndexReader.open(FSDirectory.open(new File("D:\\TermIndex")));
					result.setText("Term query result:\n");
				}
				
				Long time = System.currentTimeMillis();
		    	IndexSearcher searcher = new IndexSearcher(reader);
				TopDocs docs = searcher.search(query, 10);
				result.append("Number of hits: "+docs.totalHits);
				result.append("\nTime taken: "+ (System.currentTimeMillis()-time));
				result.append("\n======================================================================");
				
				ScoreDoc[] sd = docs.scoreDocs;
				for(int i=0; i<sd.length; i++)
				{
					String path = searcher.doc(sd[i].doc).get("path");
					result.append("\n"+ path);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	
}
