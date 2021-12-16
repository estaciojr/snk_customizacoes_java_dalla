package br.com.dalla.deive.util;

public class Main {
	/*public static void main(String[] args) {
		LocalDate dataDeDemissao = LocalDate.of(2020, 4, 10);
		LocalDate dataDeHojeSubtraida = LocalDate.now();
		
		dataDeHojeSubtraida = dataDeHojeSubtraida.minusMonths(20);
		
		System.out.println("Data de demissão: " + dataDeDemissao);
		System.out.println("20 meses atrás a partir de hoje: " + dataDeHojeSubtraida);
		
		if (dataDeHojeSubtraida.compareTo(dataDeDemissao) == 1) {
			System.out.println("Pode contratar novamente!");
		} else {
			System.out.println("Não pode contratar!");
		}
		
		ArrayList<Integer> empresas = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
		System.out.println(empresas.contains(2));
		
	}*/
	
	public static void main(String[] args) {
		String descricaoBairro = "Meu Bonito Bairro";
		
		System.out.println(descricaoBairro);
		
		char ultimoCharactere = getUltimoCharactere(descricaoBairro);
		boolean ultimoCharactereEhEspaco = ultimoCharacterEhEspaco(ultimoCharactere);
		System.out.println("Último character: " + ultimoCharactere + ". É espaço? " + ultimoCharactereEhEspaco);
	}
	
	public static char getUltimoCharactere(String descricaoDoBairro) {
		char[] descricaoBairroCharacters = descricaoDoBairro.toCharArray();
		int ultimaPosicao = descricaoBairroCharacters.length - 1;
		return descricaoBairroCharacters[ultimaPosicao];
	}
	
	public static boolean ultimoCharacterEhEspaco(char ultimoCharactere) {
		return ultimoCharactere == ' ';
	}
	
}
