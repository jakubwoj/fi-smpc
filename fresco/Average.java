import java.math.BigInteger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.ProtocolFactory;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.configuration.CmdLineUtil;
import dk.alexandra.fresco.framework.sce.SCE;
import dk.alexandra.fresco.framework.sce.SCEFactory;
import dk.alexandra.fresco.framework.sce.configuration.ProtocolSuiteConfiguration;
import dk.alexandra.fresco.framework.sce.configuration.SCEConfiguration;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.builder.NumericIOBuilder;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;
import dk.alexandra.fresco.lib.helper.sequential.SequentialProtocolProducer;

public class Average implements Application {
	
	private static final long serialVersionUID = 2055835806798696869L;
	
	private final int numPeers;
	
	private final BigInteger input;

	private OInt outputSum;

	
	public Average(SCEConfiguration sceConf, int input) {
		this.numPeers = sceConf.getParties().size();
		this.input = BigInteger.valueOf(input);
	}

	public int getNumPeers() {
		return numPeers;
	}

	/**
	 * The value is only available after the application has run.
	 */
	public BigInteger getOutputSum() {
		return outputSum.getValue();
	}
	

	public static void main(String[] args) {
		CmdLineUtil util = new CmdLineUtil();
		util.addOption(Option.builder("in")
		        .desc("Input value")
		        .longOpt("inputc").required(true).hasArg().build());
		
		CommandLine cmd = util.parse(args);
		int input = Integer.parseInt(cmd.getOptionValue("in"));
		
		SCEConfiguration sceConf = util.getSCEConfiguration();
		ProtocolSuiteConfiguration psConf = util.getProtocolSuiteConfiguration();
		SCE sce = SCEFactory.getSCEFromConfiguration(sceConf, psConf);
		
		Average app = new Average(sceConf, input);
		
		runComputation(app, sce);
	}

	private static void runComputation(Average app, SCE sce) {
		long start = System.currentTimeMillis();
		
		sce.runApplication(app);
		
		long end = System.currentTimeMillis();
		System.out.println("Complete computation time is : " + (end - start) + " ms");
		
		double numPeers = app.getNumPeers();
		double average = app.getOutputSum().doubleValue() / numPeers;
		
		System.out.println("Sum: " + app.getOutputSum());
		System.out.println("Average: " + average);
	}

	@Override
	public ProtocolProducer prepareApplication(ProtocolFactory factory) {
		final BasicNumericFactory numFactory = (BasicNumericFactory) factory;
	    
	    // Get the secret input values from all players
	    NumericIOBuilder ioBuilder = new NumericIOBuilder(numFactory);
	    
	    SInt[] secretInputs = new SInt[numPeers];
	    
	    ioBuilder.beginParScope();
	    {
	    	for (int p = 1; p <= numPeers; p++) {
		    	secretInputs[p - 1] = ioBuilder.input(this.input, p);
		    }
	    }
	    ioBuilder.endCurScope();
	    
	    ProtocolProducer secretInputsProtocol = ioBuilder.getProtocol();
	    
	    // Compute the sum
	    NumericProtocolBuilder numProtocolBuilder = new NumericProtocolBuilder(numFactory);
	    
	    SInt secretSum = numProtocolBuilder.sum(secretInputs);
	    
	    ProtocolProducer secretSumProtocol = numProtocolBuilder.getProtocol();
	    
	    // Open the secret sum
	    ioBuilder.reset();
	    
	    this.outputSum = ioBuilder.output(secretSum);
	    
	    ProtocolProducer openProtocol = ioBuilder.getProtocol();

	    // Perform the protocols defined above in sequential order
	    ProtocolProducer protocol = new SequentialProtocolProducer(
	    		secretInputsProtocol,
	    		secretSumProtocol,
	    		openProtocol);
	    
		return protocol;
	}
	
}