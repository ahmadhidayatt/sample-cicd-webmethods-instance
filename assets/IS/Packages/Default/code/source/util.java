

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.util.coder.IDataJSONCoder;
import java.util.*;
import java.io.ByteArrayOutputStream;
// --- <<IS-END-IMPORTS>> ---

public final class util

{
	// ---( internal utility methods )---

	final static util _instance = new util();

	static util _newInstance() { return new util(); }

	static util _cast(Object o) { return (util)o; }

	// ---( server methods )---




	public static final void compareDocList (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(compareDocList)>> ---
		// @sigtype java 3.5
		// [i] record:1:required docListA
		// [i] record:1:required docListB
		// [o] object:0:required isEqual
		IDataCursor cursor = pipeline.getCursor();
			IData[] listA = IDataUtil.getIDataArray(cursor, "docListA");
			IData[] listB = IDataUtil.getIDataArray(cursor, "docListB");
			
			boolean isEqual = true;
			
			try {
			    if (listA == null && listB == null) {
			        isEqual = true;
			    } else if (listA == null || listB == null) {
			        isEqual = false;
			    } else if (listA.length != listB.length) {
			        isEqual = false;
			    } else {
		
			        IDataJSONCoder coder = new IDataJSONCoder();
		
			        for (int i = 0; i < listA.length; i++) {
		
			            IData docA = normalize(listA[i]);
			            IData docB = normalize(listB[i]);
		
			            // FIX: encode JSON (compatible semua versi)
			            java.io.ByteArrayOutputStream baosA = new java.io.ByteArrayOutputStream();
			            coder.encode(baosA, docA);
			            String jsonA = baosA.toString();
		
			            java.io.ByteArrayOutputStream baosB = new java.io.ByteArrayOutputStream();
			            coder.encode(baosB, docB);
			            String jsonB = baosB.toString();
		
			            if (!jsonA.equals(jsonB)) {
			                isEqual = false;
			                break;
			            }
			        }
			    }
		
			} catch (Exception e) {
			    throw new ServiceException(e);
			}
		
			IDataUtil.put(cursor, "isEqual", isEqual);
			cursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void reconcileDocList (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(reconcileDocList)>> ---
		// @sigtype java 3.5
		// [i] record:1:required docListA
		// [i] record:1:required docListB
		// [o] record:1:required missingApps
		// [o] record:1:required changedApps
		// [o] record:1:required newApps
		IDataCursor cursor = pipeline.getCursor();
		IData[] listA = IDataUtil.getIDataArray(cursor, "docListA"); // PRE
		IData[] listB = IDataUtil.getIDataArray(cursor, "docListB"); // POST
		
		List<IData> missingApps = new ArrayList<>();
		List<IData> changedApps = new ArrayList<>();
		List<IData> newApps = new ArrayList<>();
		
		try {
		
			Map<String, IData> mapA = toMapById(listA);
			Map<String, IData> mapB = toMapById(listB);
		
			IDataJSONCoder coder = new IDataJSONCoder();
		
			// \uD83D\uDD39 CHECK PRE \u2192 POST
			for (String id : mapA.keySet()) {
		
				IData appA = normalize(mapA.get(id));
				IData appB = mapB.get(id);
		
				if (appB == null) {
					
					missingApps.add(appA);
				} else {
					appB = normalize(appB);
		
					String jsonA = toJson(coder, appA);
					String jsonB = toJson(coder, appB);
		
					if (!jsonA.equals(jsonB)) {
		
					    if (isApiReduced(appA, appB)) {
					        changedApps.add(appA);
					    }
					    
					}
				}
			}
		
			// \uD83D\uDD39 CHECK NEW (POST only)
			for (String id : mapB.keySet()) {
				if (!mapA.containsKey(id)) {
					newApps.add(mapB.get(id));
				}
			}
		 
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		// OUTPUT
		IDataUtil.put(cursor, "missingApps", missingApps.toArray(new IData[0]));
		IDataUtil.put(cursor, "changedApps", changedApps.toArray(new IData[0]));
		IDataUtil.put(cursor, "newApps", newApps.toArray(new IData[0]));
		
		cursor.destroy();
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static IData normalize(IData doc) {
		if (doc == null) return null;
	
		IDataCursor c = doc.getCursor();
	
		// remove field tidak penting
		IDataUtil.remove(c, "identifiers");
		IDataUtil.remove(c, "authStrategyIds");
		IDataUtil.remove(c, "created");
		IDataUtil.remove(c, "lastupdated");
		IDataUtil.remove(c, "accessTokens");
	
		// handle teams biar konsisten
		IData teams = IDataUtil.getIData(c, "teams");
		if (teams != null) {
		    IDataCursor tc = teams.getCursor();
		    Object rootArray = IDataUtil.get(tc, "$rootArray");
	
		    if (rootArray == null) {
		        IDataUtil.put(tc, "$rootArray", new Object[0]);
		    }
	
		    tc.destroy();
		}
	
		removeNulls(doc);
	
		c.destroy();
		return doc;
	}
	private static void removeNulls(IData doc) {
		if (doc == null) return;
	
		IDataCursor cursor = doc.getCursor();
		java.util.List<String> toRemove = new java.util.ArrayList<>();
	
		while (cursor.next()) {
		    String key = cursor.getKey();
		    Object value = cursor.getValue();
	
		    if (value == null) {
		        toRemove.add(key);
		    } else if (value instanceof IData) {
		        removeNulls((IData) value);
		    } else if (value instanceof IData[]) {
		        for (IData d : (IData[]) value) {
		            removeNulls(d);
		        }
		    }
		}
	
		cursor.destroy();
	
		cursor = doc.getCursor();
		for (String key : toRemove) {
		    IDataUtil.remove(cursor, key);
		}
		cursor.destroy();
	}
	private static Map<String, IData> toMapById(IData[] list) {
		Map<String, IData> map = new HashMap<>();
		if (list == null) return map;
	
		for (IData doc : list) {
			IDataCursor c = doc.getCursor();
			String id = IDataUtil.getString(c, "id");
			c.destroy();
	
			if (id != null) {
				map.put(id, doc);
			}
		}
		return map;
	}
	private static String toJson(IDataJSONCoder coder, IData doc) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		coder.encode(baos, doc);
		return baos.toString();
	}
	private static boolean isApiReduced(IData appA, IData appB) {
	    
	    Set<String> setA = getStringSet(appA, "consumingAPIs");
	    Set<String> setB = getStringSet(appB, "consumingAPIs");
	
	    
	    return !setB.containsAll(setA);
	}
	private static Set<String> getStringSet(IData doc, String field) {
	    Set<String> result = new HashSet<>();
	
	    IDataCursor c = doc.getCursor();
	    String[] arr = IDataUtil.getStringArray(c, field);
	    c.destroy();
	
	    if (arr != null) {
	        result.addAll(Arrays.asList(arr));
	    }
	
	    return result;
	}
	// --- <<IS-END-SHARED>> ---
}

