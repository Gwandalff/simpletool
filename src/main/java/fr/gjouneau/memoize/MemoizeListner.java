package fr.gjouneau.memoize;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.EventContext;
import com.oracle.truffle.api.instrumentation.ExecutionEventListener;
import com.oracle.truffle.api.nodes.Node;

public class MemoizeListner implements ExecutionEventListener {
	
	private Map<Node,Map<Object[],Object>> memoizeTable = new HashMap();
	
	private Deque<Boolean> inMemory = new ArrayDeque<>();
	private Deque<Object[]> effectiveArguments = new ArrayDeque<>();
	private Object memoizedValue = null;

	@Override
	public void onEnter(EventContext context, VirtualFrame frame) {
		Node call = context.getInstrumentedNode();
		Object[] args = frame.getArguments();
		
		effectiveArguments.addLast(args);
		
		Map<Object[],Object> table = memoizeTable.get(call);	
		if(table == null) {
			memoizeTable.put(call, new HashMap<Object[],Object>());
			inMemory.addLast(false);
		} else {
			memoizedValue = table.get(effectiveArguments.getLast());
			boolean inBase = memoizedValue != null;
			inMemory.addLast(inBase);
			if(inBase) context.createUnwind(null);
		}
	}

	@Override
	public void onReturnValue(EventContext context, VirtualFrame frame, Object result) {
		Node call = context.getInstrumentedNode();
		Map<Object[],Object> table = memoizeTable.get(call);
		if(!inMemory.getLast()) {
			table.put(effectiveArguments.removeLast(), result);
			memoizeTable.put(call, table);
		} else {
			effectiveArguments.removeLast();
		}
		inMemory.removeLast();
	}
	
	@Override
	public Object onUnwind(EventContext context, VirtualFrame frame, Object info) {
        return memoizedValue;
    }

	@Override
	public void onReturnExceptional(EventContext context, VirtualFrame frame, Throwable exception) {
		// TODO Auto-generated method stub
		
	}

}
