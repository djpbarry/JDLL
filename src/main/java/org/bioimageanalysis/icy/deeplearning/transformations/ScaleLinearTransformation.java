package org.bioimageanalysis.icy.deeplearning.transformations;

import org.bioimageanalysis.icy.deeplearning.tensor.Tensor;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class ScaleLinearTransformation extends AbstractTensorPixelTransformation
{

	private static final String name = "scale_linear";
	private Double gainDouble;
	private Double offsetDouble;
	private double[] gainArr;
	private double[] offsetArr;
	private String axes;

	public ScaleLinearTransformation()
	{
		super( name );
	}
	
	public void setGain(double gain) {
		this.gainDouble = gain;
	}
	
	public void setGain(double[] gain) {
		this.gainArr = gain;
	}
	
	public void setGain(double[][] gain) {
		//TODO allow scaling of more complex structures
		throw new IllegalArgumentException("At the moment, only allowed scaling of planes.");
	}
	
	public void setOffset(double offset) {
		this.offsetDouble = offset;
	}
	
	public void setOffset(double[] offset) {
		this.offsetArr = offset;
	}
	
	public void setOffset(double[][] offset) {
		//TODO allow scaling of more complex structures
		throw new IllegalArgumentException("At the moment, only allowed scaling of planes.");
	}
	
	public void setAxes(String axes) {
		this.axes = axes;
	}
	
	public void checkRequiredArgs() {
		if (offsetDouble == null && offsetArr == null) {
			throw new IllegalArgumentException(String.format(DEFAULT_MISSING_ARG_ERR, "offset"));
		} else if (gainDouble == null && gainArr == null) {
			throw new IllegalArgumentException(String.format(DEFAULT_MISSING_ARG_ERR, "gain"));
		} else if ((offsetDouble == null && gainDouble != null)
				|| (offsetDouble != null && gainDouble == null)) {
			throw new IllegalArgumentException("Both arguments 'gain' and "
					+ "'offset' need to be of the same type.");
		}
	}

	public < R extends RealType< R > & NativeType< R > > Tensor< FloatType > apply( final Tensor< R > input )
	{
		checkRequiredArgs();
		String selectedAxes = "";
		for (String ax : input.getAxesOrderString().split("")) {
			if (axes != null && !axes.toLowerCase().contains(ax.toLowerCase())
					&& !ax.toLowerCase().equals("b"))
				selectedAxes += ax;
		}
		if (axes == null || selectedAxes.equals("") 
				|| input.getAxesOrderString().replace("b", "").length() - selectedAxes.length() == 1) {
			if (gainDouble == null)
				throw new IllegalArgumentException("The 'axes' parameter is not"
						+ " compatible with the parameters 'gain' and 'offset'."
						+ "The parameters gain and offset cannot be arrays with"
						+ " the given axes parameter provided.");
			super.setFloatUnitaryOperator( v -> gainDouble.floatValue() * v + offsetDouble.floatValue() );
		} else if (input.getAxesOrderString().replace("b", "").length() - selectedAxes.length() == 2) {
			int[] iterAxes = new int[selectedAxes.length()];
			for (int i = 0; i < selectedAxes.length(); i ++)
				iterAxes[i] = input.getAxesOrderString().indexOf(selectedAxes.split("")[i]);
			long nIter = 1;
			for (int i = 0; i < iterAxes.length; i ++) {
				nIter *= input.getData().dimension(iterAxes[i]);
			}
			if (iterAxes.length == 1) {
				for (int i = 0; i < input.getData().dimension(iterAxes[0]); i ++) {
					IntervalView<R> plane = Views.interval( input.getData(), new long[] { 200, 200 }, new long[]{ 500, 350 } );
					gainDouble = gainArr[i];
					offsetDouble = offsetArr[i];
					applyInPlaceToRaiWithFun((RandomAccessibleInterval<FloatType>) plane, 
							(v -> gainDouble.floatValue() * v + offsetDouble.floatValue()));
				}
			} else if (iterAxes.length == 2) {
				for (int i = 0; i < input.getData().dimension(iterAxes[0]); i ++) {
					for (int j = 0; j < input.getData().dimension(iterAxes[1]); j ++) {
						IntervalView<R> plane = Views.interval( input.getData(), new long[] { 200, 200 }, new long[]{ 500, 350 } );
						gainDouble = gainArr[i];
						offsetDouble = offsetArr[i];
						applyInPlaceToRaiWithFun((RandomAccessibleInterval<FloatType>) plane, 
								(v -> gainDouble.floatValue() * v + offsetDouble.floatValue()));
					}
				}
			} else {
				//TODO allow scaling of more complex structures
				throw new IllegalArgumentException("At the moment, only allowed scaling of planes in"
						+ " tensors with at most 5 dimensions (including batch dimension).");
			}
				
		} else {
			//TODO allow scaling of more complex structures
			throw new IllegalArgumentException("At the moment, only allowed scaling of planes.");
		}
		return super.apply(input);
	}

	public void applyInPlaceToRaiWithFun( final RandomAccessibleInterval< FloatType > input, 
											FloatUnaryOperator fun )
	{
		LoopBuilder
				.setImages( input )
				.multiThreaded()
				.forEachPixel( i -> i.set( fun.applyAsFloat( i.get() ) ) );
	}

	public void applyInPlace( final Tensor< FloatType > input )
	{
		checkRequiredArgs();
		super.apply(input);
	}
}
