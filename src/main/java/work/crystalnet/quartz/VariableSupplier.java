package work.crystalnet.quartz;

import java.util.HashSet;
import java.util.Set;

public class VariableSupplier {

	public interface Supplier {

		String request(String key);
	}

	private static Set<Supplier> suppliers = new HashSet<>();

	public static Set<Supplier> getSuppliers() {
		return suppliers;
	}

	public static String variable(String key) {
		String result = null;
		for (Supplier supplier : suppliers) {
			String get = supplier.request(key);
			if (get != null) {
				if (result != null)
					throw new IllegalStateException("変数の供給箇所が２つ以上あります");
				result = get;
			}
		}
		return result != null ? result : "null";
	}
}
