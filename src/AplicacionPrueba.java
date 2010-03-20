
public class AplicacionPrueba {

	public static void main(String[] args) {
		for(int k = 0; k < 10; k++) {
			for(int i = 1; i < 10; i++) {
				StringBuilder sb = new StringBuilder();
				for(int j = 0; j < i; j++) {
					sb.append("..");
				}
				System.out.println(sb.toString());
			}
		}
	}
}
