package fr.gjouneau.memoize;

import com.oracle.truffle.api.instrumentation.Instrumenter;
import com.oracle.truffle.api.instrumentation.SourceSectionFilter;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.TruffleInstrument;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Registration;

@Registration(id = MemoizeInstrument.ID, name = "Dynamic Memoization Module", version = "0.1", services = MemoizeInstrument.class)
public class MemoizeInstrument extends TruffleInstrument {

	public static final String ID = "dynamic-memoization-module";

	@Override
	protected void onCreate(Env env) {
		SourceSectionFilter.Builder builder = SourceSectionFilter.newBuilder();
		SourceSectionFilter filter = builder.tagIs(StandardTags.CallTag.class).build();
		Instrumenter instrumenter = env.getInstrumenter();
		instrumenter.attachExecutionEventListener(filter, new MemoizeListner());
	}

}
