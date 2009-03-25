/*
 * Copyright 2005, 2009 Cosmin Basca.
 * e-mail: cosmin.basca@gmail.com
 * 
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.MedianFilterDescriptor;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import robo.gui.BufferedImageDisplayerPanel;
import robo.gui.HoughEllipseConfigurationPanel;
import robo.gui.icons.Icons;
import robo.vision.EllipseDescriptor;
import robo.vision.JAIOperatorRegister;
import robo.vision.widgets.VisionUtils;

public class EllipseDetector extends JFrame implements ActionListener {

	private static final long serialVersionUID = -8237018495582668882L;
	private RenderedImage mask;
	private BufferedImageDisplayerPanel originalImage;
	private BufferedImageDisplayerPanel edgeDetector;
	private BufferedImageDisplayerPanel adaptiveThreshold;
	private BufferedImageDisplayerPanel ellipseDetection;
	private HoughEllipseConfigurationPanel ellipseCfg;
	private JToolBar toolbar;
	private JButton open;
	private JButton detect;
	private JButton exit;
	private JButton about;
	private JTabbedPane tabs;
	private JTextArea console;
	private JProgressBar progress;

	private PlanarImage source;

	private String CMD_OPEN = "CMD_OPEN";
	private String CMD_EXIT = "CMD_EXIT";
	private String CMD_DETECT = "CMD_DETECT";
	private String CMD_ABOUT = "CMD_ABOUT";

	private double border = 3.0;
	private double x = border;
	private double y = border;

	private class EllipseDetectionData {
		public BufferedImage greyScale;
		public BufferedImage medianFilter;
		public BufferedImage sobel;
		public BufferedImage edge;
		public BufferedImage binaryThreshold;
		public BufferedImage detectedEllipses;
		public List<EllipseDescriptor> ellipses;
		double iterativeThreshold;
		double maxEntropyThreshold;
		double maxVarianceThreshold;
		double minErrorThreshold;
		double minFuzzinessThreshold;
		double entropy;
		double mean;
		
		public String info() {
			String inf = "";
			inf += "------------------------------------------------------------------------------\n";
			inf += "IterativeThreshold \t: "+iterativeThreshold+"\n";
			inf += "MaxEntropyThreshold \t: "+maxEntropyThreshold+"\n";
			inf += "MaxVarianceThreshold \t: "+maxVarianceThreshold+"\n";
			inf += "MinErrorThreshold \t: "+minErrorThreshold+"\n";
			inf += "MinFuzzinessThreshold \t: "+minFuzzinessThreshold+"\n";
			inf += "Entropy \t\t: "+entropy+"\n";
			inf += "Mean \t\t: "+mean+"\n";
			for(EllipseDescriptor ellipse:ellipses) {
				inf += "------------------------------------------------------------------------------\n";
				inf += ellipse.toString()+"\n";
			}
			return inf;
		}
	}

	public EllipseDetector() {
		this.setTitle("Randomized Hough Ellipse Detector");
		// this.setLayout(new GridLayout(2,2));
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setVisible(true);

		this.toolbar = new JToolBar();

		this.open = new JButton();
		this.open.setActionCommand(CMD_OPEN);
		this.open.addActionListener(this);
		this.open.setIcon(Icons.iconOpen);

		this.detect = new JButton();
		this.detect.setActionCommand(CMD_DETECT);
		this.detect.addActionListener(this);
		this.detect.setIcon(Icons.iconDetect);

		this.about = new JButton();
		this.about.setActionCommand(CMD_ABOUT);
		this.about.addActionListener(this);
		this.about.setIcon(Icons.iconAbout);

		this.exit = new JButton();
		this.exit.setActionCommand(CMD_EXIT);
		this.exit.addActionListener(this);
		this.exit.setIcon(Icons.iconExit);

		this.toolbar.add(open);
		this.toolbar.addSeparator();
		this.toolbar.add(detect);
		this.toolbar.add(about);
		this.toolbar.add(exit);
		this.add(this.toolbar, BorderLayout.NORTH);

		JPanel topBox = new JPanel(new GridLayout(1, 2));
		Dimension formatSize = new Dimension(200, 200);
		if (this.source != null) {
			formatSize = new Dimension(this.source.getWidth(), this.source
					.getHeight());
		}

		ParameterBlock pb = new ParameterBlock();
		pb.add(59); // minA - big radius range
		pb.add(71); // maxA
		pb.add(59); // minB - small radius range
		pb.add(71); // maxB
		pb.add(100); // the quality of the detected ellipse
		pb.add(5000); // threshold
		ellipseCfg = new HoughEllipseConfigurationPanel(formatSize);
		ellipseCfg.setParameters(pb);

		originalImage = new BufferedImageDisplayerPanel();
		originalImage.setPreferredSize(formatSize);
		topBox.add(ellipseCfg);
		topBox.add(originalImage);

		this.add(topBox, BorderLayout.SOUTH);

		JPanel results = new JPanel(new GridLayout(2, 2));

		progress = new JProgressBar();
		JPanel progrepanel = new JPanel(new FlowLayout());
		progrepanel.add(progress);
		results.add(progrepanel);
		
		edgeDetector = new BufferedImageDisplayerPanel();
		edgeDetector.setPreferredSize(formatSize);
		results.add(edgeDetector);

		adaptiveThreshold = new BufferedImageDisplayerPanel();
		adaptiveThreshold.setPreferredSize(formatSize);
		results.add(adaptiveThreshold);

		ellipseDetection = new BufferedImageDisplayerPanel();
		ellipseDetection.setPreferredSize(formatSize);
		results.add(ellipseDetection);

		console = new JTextArea();
		console.setEditable(false);
		console.setLineWrap(true);

		tabs = new JTabbedPane();
		tabs.addTab("Processing steps", results);
		tabs.addTab("Processing results", new JScrollPane(console));

		this.add(tabs, BorderLayout.CENTER);
		this.pack();

		createMask(formatSize);
	}

	private void createMask(Dimension formatSize) {
		double w = formatSize.width - 2 * border;
		double h = formatSize.height - 2 * border;
		mask = new BufferedImage(formatSize.width, formatSize.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) ((BufferedImage) mask).getGraphics();
		graphics.setColor(Color.white);
		graphics.fill(new Rectangle2D.Double(x, y, w, h));
		graphics.dispose();
	}

	private BufferedImage getImage(PlanarImage image) {
		BufferedImage buffer = new BufferedImage(image.getWidth(), image
				.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) buffer.getGraphics();
		graphics.drawImage(image.getAsBufferedImage(), 0, 0, null);
		graphics.dispose();
		return buffer;
	}

	public void detectAllEllipses() {
		class EllipseFinder extends SwingWorker<EllipseDetectionData, Object> {
		       @Override
		       public EllipseDetectionData doInBackground() {
		           return detectEllipses();
		       }

		       @Override
		       protected void done() {
		           try {
		        	   progress.setIndeterminate(false);
		        	   EllipseDetectionData data  = get();
		               EllipseDetector.this.adaptiveThreshold.setImage(data.binaryThreshold);
		               EllipseDetector.this.edgeDetector.setImage(data.edge);
		               EllipseDetector.this.ellipseDetection.setImage(data.detectedEllipses);
		               EllipseDetector.this.console.setText(data.info());
		           } catch (Exception ignore) {
		           }
		       }
		   }
		(new EllipseFinder()).execute();
	}
	
	public EllipseDetectionData detectEllipses() {
		EllipseDetectionData data = new EllipseDetectionData();
		// snapshot - rendered image as argument
		List<EllipseDescriptor> ellipses = new ArrayList<EllipseDescriptor>();
		if (this.source == null)
			return data;

		PlanarImage img = this.source;
		ParameterBlock pb = new ParameterBlock();

		float scale = 1.0F;

		// ******************************************
		// * GREYSCALE ***
		// ******************************************
		img = VisionUtils.convertColorToGray(img, 0);
		data.greyScale = getImage(img);
		// ******************************************
		// * MEDIAN FILTERING ***
		// ******************************************
		pb = new ParameterBlock();
		pb.addSource(img);
		pb.add(MedianFilterDescriptor.MEDIAN_MASK_X);
		pb.add(3);
		img = (PlanarImage) JAI.create("MedianFilter", pb);
		data.medianFilter = getImage(img);
		// ******************************************
		// * SCALE ***
		// ******************************************
		pb = new ParameterBlock();
		pb.addSource(img);
		pb.add(scale);
		pb.add(scale);
		img = (PlanarImage) JAI.create("Scale", pb);
		// ******************************************
		// * EDGE DETECT SOBEL ***
		// ******************************************
		pb = new ParameterBlock();
		pb.addSource(img);
		pb.add(robo.vision.Kernel.SOBEL_H);
		pb.add(robo.vision.Kernel.SOBEL_V);

		img = (PlanarImage) JAI.create("GradientMagnitude", pb);
		data.sobel = getImage(img);

		// ******************************************
		// * DEL BORDER ***
		// ******************************************
		pb = new ParameterBlock();
		pb.addSource(img);
		pb.addSource(mask);
		img = (PlanarImage) JAI.create("And", pb);
		
		data.edge = getImage(img);
		// ******************************************
		// * ADAPTIVE TRESHOLD MEDIAN ***
		// ******************************************
		/*
		 * pb = new ParameterBlock(); pb.addSource(img);
		 * pb.setParameters(thresholdBlock.getParameters());
		 * 
		 * img =(PlanarImage)JAI.create("AdaptiveThreshold", pb); //
		 */
		// /*
		int[] bins = { 256 };
		double[] low = { 0.0D };
		double[] high = { 256.0D };

		pb = new ParameterBlock();
		pb.addSource(img);
		pb.add(null);
		pb.add(1);
		pb.add(1);
		pb.add(bins);
		pb.add(low);
		pb.add(high);

		RenderedOp op = JAI.create("histogram", pb);
		Histogram histogram = (Histogram) op.getProperty("histogram");
		// histogram = histogram.getSmoothed(true,20);

		double t1 = histogram.getIterativeThreshold()[0];
		double t2 = histogram.getMaxEntropyThreshold()[0];
		double t3 = histogram.getMaxVarianceThreshold()[0];
		double t4 = histogram.getMinErrorThreshold()[0];
		double t5 = histogram.getMinFuzzinessThreshold()[0];
		double t6 = histogram.getEntropy()[0];
		double t7 = histogram.getMean()[0];

		data.iterativeThreshold = t1;
		data.maxEntropyThreshold = t2;
		data.maxVarianceThreshold = t3;
		data.minErrorThreshold = t4;
		data.minFuzzinessThreshold = t5;
		data.entropy = t6;
		data.mean = t7;
		
		System.out.println("===========================================================");
		System.out.println(t1);
		System.out.println(t2);
		System.out.println(t3);
		System.out.println(t4);
		System.out.println(t5);
		System.out.println(t6);
		System.out.println(t7);
		System.out.println("===========================================================");

		// --------------------------------
		// PALY WITH THESE PARAMS
		// can select other threshold from tN above
		// the threshold here selects the amount of pixels to use
		// in the detection process later on, so choose one that is adequate
		// --------------------------------
		pb = new ParameterBlock();
		pb.addSource(img);
		pb.add(new double[] { t3 });
		pb.add(new double[] { 255 });

		img = (PlanarImage) JAI.create("BinaryThreshold", pb);
		data.binaryThreshold = getImage(img);
		// */

		// ******************************************
		// * HOUGH TRANSF ***
		// ******************************************
		ellipses = this.findEllipsesAt(img, scale, data);
		data.ellipses = ellipses;
		return data;
	}

	@SuppressWarnings("unchecked")
	private List<EllipseDescriptor> findEllipsesAt(PlanarImage src, float scale, EllipseDetectionData data) {
		List<EllipseDescriptor> ellipses = null;
		ParameterBlock pb = ellipseCfg.getParameters();
		pb.addSource(src);

		PlanarImage eht = (PlanarImage) JAI.create("HoughEllipses", pb);
		data.detectedEllipses = getImage(eht);

		ellipses = (List<EllipseDescriptor>) eht
				.getProperty(EllipseDescriptor.DETECTED_ELLIPSES);

		return ellipses;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 	com.jgoodies.looks.windows.ExtWindowsLookAndFeel
			com.jgoodies.looks.plastic.PlasticLookAndFeel
			com.jgoodies.looks.plastic.Plastic3DLookAndFeel
			com.jgoodies.looks.plastic.PlasticXPLookAndFeel
		 */
		/*try {
		    UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		} catch (Exception e) {
			System.out.println("Error setting the LNF");
			System.out.println(e);
		}*/
		JAIOperatorRegister.registerOperators();
		new EllipseDetector();
	}

	protected void openImage(File src) {
		this.source = (PlanarImage) JAI.create("fileload", src
				.getAbsolutePath());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == CMD_ABOUT) {
			JOptionPane
					.showMessageDialog(
							this,
							"Copyright (c) 2005, 2009 Cosmin Basca \n cosmin.basca@gmail.com",
							"About RHED - Randomized HOUGH Ellipse Detector",
							JOptionPane.INFORMATION_MESSAGE);
		} else if (cmd == CMD_EXIT) {
			System.exit(0);
		} else if (cmd == CMD_DETECT) {
			edgeDetector.clearImage();
			adaptiveThreshold.clearImage();
			ellipseDetection.clearImage();
			
			progress.setIndeterminate(true);
			this.detectAllEllipses();
		} else if (cmd == CMD_OPEN) {
			JFileChooser fc = new JFileChooser(new File(System
					.getProperty("user.dir")));
			fc.showOpenDialog(this);
			File file = fc.getSelectedFile();
			if (file != null) {
				this.openImage(file);
				Dimension size = new Dimension(source.getWidth(), source.getHeight());
				originalImage.setPreferredSize(size);
				originalImage.setMaximumSize(size);
				originalImage.setMinimumSize(size);

				edgeDetector.setPreferredSize(size);
				edgeDetector.setMaximumSize(size);
				edgeDetector.setMinimumSize(size);

				adaptiveThreshold.setPreferredSize(size);
				adaptiveThreshold.setMaximumSize(size);
				adaptiveThreshold.setMinimumSize(size);

				ellipseDetection.setPreferredSize(size);
				ellipseDetection.setMaximumSize(size);
				ellipseDetection.setMinimumSize(size);

				this.originalImage.setImage(this.source);
				this.createMask(size);
				this.pack();
			}
		}
	}

}
