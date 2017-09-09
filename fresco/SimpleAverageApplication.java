import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
import dk.alexandra.fresco.lib.helper.ParallelProtocolProducer;
import dk.alexandra.fresco.lib.helper.builder.NumericIOBuilder;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;
import dk.alexandra.fresco.lib.helper.sequential.SequentialProtocolProducer;

public class SimpleAverageApplication implements Application {
  private static int INPUT_COUNT;

  private static final long serialVersionUID = -24333164590218997L;

  private SCEConfiguration sceConf;
  public static int PARALLEL_COUNT;

  private BigInteger[] myInput;
  private OInt[] outputs;

  public SimpleAverageApplication(SCEConfiguration sceConf) {
    this.sceConf = sceConf;
    this.myInput = new BigInteger[1];
  }

  public void setValues(long[] inputValues) {
    this.myInput = new BigInteger[inputValues.length];
    for (int i = 0; i < inputValues.length; i++) {
      this.myInput[i] = BigInteger.valueOf(inputValues[i]);
    }
  }

  private void setValues(BigInteger[] inputValues) {
    this.myInput = new BigInteger[inputValues.length];
    for (int i = 0; i < inputValues.length; i++) {
      this.myInput[i] = inputValues[i];
    }
  }

  public void setValue(long myValue) {
    this.myInput[0] = BigInteger.valueOf(myValue);
  }

  public void setValue(BigInteger myValue) {
    this.myInput[0] = myValue;
  }

  @Override
  public ProtocolProducer prepareApplication(ProtocolFactory factory) {
    return prepareApplicationSequential(factory);
  }

  private ProtocolProducer prepareApplicationSequential(
      ProtocolFactory factory) {
    final int numPeers = sceConf.getParties().size();

    BasicNumericFactory fac = (BasicNumericFactory) factory;
    NumericIOBuilder ioBuilder = new NumericIOBuilder(fac);
    NumericProtocolBuilder npb = new NumericProtocolBuilder(fac);
    SInt[] inputSharings = new SInt[numPeers];
    ioBuilder.beginParScope();
    for (int p = 1; p <= numPeers; p++) {
      inputSharings[p - 1] = ioBuilder.input(this.myInput[0], p);
    }
    ioBuilder.endCurScope();
    ProtocolProducer closeInputProtocol = ioBuilder.getProtocol();
    ioBuilder.reset();

    // create wire
    SInt ssum = npb.sum(inputSharings);

    // create Sequence of protocols which eventually
    // will compute the sum
    ProtocolProducer sumProtocol = npb.getProtocol();

    this.outputs = new OInt[] { ioBuilder.output(ssum) };
    ProtocolProducer openProtocol = ioBuilder.getProtocol();

    ProtocolProducer gp = new SequentialProtocolProducer(
        closeInputProtocol, sumProtocol, openProtocol);
    return gp;
      }


  public OInt[] getResult() {
    return this.outputs;
  }

  public static void main(String[] args) throws Exception {
    doMain(args);
    System.exit(0);
  }

  private static void doMain(String[] args) throws IOException {
    CmdLineUtil util = new CmdLineUtil();
    util.addOption(Option.builder("d")
        .desc("The delay for the timer executing the protocol")
        .longOpt("delay").required(false).hasArg().build());
    util.addOption(Option.builder("ic")
        .desc("The number of inputs to use")
        .longOpt("inputcount").required(false).hasArg().build());
    CommandLine cmd = util.parse(args);
    // System.out.println(Arrays.toString(args));
    SCEConfiguration sceConf = util.getSCEConfiguration();
    ProtocolSuiteConfiguration psConf = util
      .getProtocolSuiteConfiguration();
    SimpleAverageApplication app = createApplication(sceConf);
    SCE sce = SCEFactory.getSCEFromConfiguration(sceConf, psConf);
    final int numPeers = sceConf.getParties().size() - 1;

    if (cmd.getOptionValue("ic") != null) {
      String value = cmd.getOptionValue("ic");
      INPUT_COUNT = Integer.parseInt(value);
    } else {
      INPUT_COUNT = 1000;
    }

    PARALLEL_COUNT = 1;

    computationAsGateway(app, sce, numPeers, sceConf.getMyId());
  }

  private static SimpleAverageApplication createApplication(SCEConfiguration sceConf) {
    SimpleAverageApplication app = new SimpleAverageApplication(sceConf);
    return app;
  }


  private static void computationAsGateway(SimpleAverageApplication app, SCE sce,
      int numPeers, int myID) {
    BigInteger currentSum = BigInteger.ZERO;
    for (long dataPointCount = 0 ; dataPointCount < INPUT_COUNT; dataPointCount += 1) {
      BigInteger inputValue = BigInteger.valueOf(myID);
      app.setValue(inputValue);
      long start = System.currentTimeMillis();
      sce.runApplication(app);
      System.out.println("Complete computation time is : " + (System.currentTimeMillis() - start));
      OInt[] newSums = app.getResult();
      System.out.println("Length "+  newSums.length);
      if (currentSum == null) {
        break;
      }
      for (int i = 0; i < newSums.length; i++) {
        System.out.println("Got "+  newSums[i].getValue());
        BigInteger value = newSums[i].getValue();
        if (value != null) {
          currentSum = currentSum.add(value);
        } else {
          String date = DateFormat.getDateTimeInstance().format(new Date());
          System.out.println(date + ", Early break at : " + dataPointCount);
          throw new RuntimeException("Early break");
        }
        System.out.println("Sum is " + currentSum);
        //			}
        double avg = currentSum.doubleValue() / ((dataPointCount+1)  * numPeers);
    }
    double avg = currentSum.doubleValue() / ((dataPointCount+1) * numPeers);
    String date = DateFormat.getDateTimeInstance().format(new Date());
    System.out.println(date + ", Amount: " + INPUT_COUNT + " AVG: " + avg + " SUM: " + currentSum.doubleValue());

  }
}
}

