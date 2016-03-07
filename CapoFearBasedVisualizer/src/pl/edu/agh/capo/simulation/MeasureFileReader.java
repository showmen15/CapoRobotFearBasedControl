package pl.edu.agh.capo.simulation;

import pl.edu.agh.capo.logic.common.Measure;
import pl.edu.agh.capo.logic.common.Vision;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MeasureFileReader implements Iterator<Measure> {

    private static final int READ_JUMP = 2;     //24 for 30 readings

    private Iterator<Measure> measures;

    private List<Measure> list = new ArrayList<Measure>();

    public MeasureFileReader(String filePath) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                list.add(createMeasure(line));
            }
            measures = list.iterator();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Measure createMeasure(String fileLine) {
        String[] data = fileLine.split(";");
        List<Vision> visions = new ArrayList<Vision>();
        for (int i = 3; i < data.length; i += READ_JUMP * 2) {
            Vision vision = new Vision(Double.parseDouble(data[i + 1]), Double.parseDouble(data[i]) / 1000);
            visions.add(vision);
        }
        double leftVelocity = Double.parseDouble(data[1]);
        double rightVelocity = Double.parseDouble(data[2]);
        return new Measure(leftVelocity, rightVelocity, visions);
    }

    @Override
    public boolean hasNext() {
        return measures.hasNext();
    }

    @Override
    public Measure next() {
        if (!measures.hasNext()) {
            measures = list.iterator();
        }
        return measures.next();
    }



	@Override
	public void remove() {
		// TODO Auto-generated method stub
		System.out.println("remove -- NOT IMPLEMENTED!");
		
	}

}
