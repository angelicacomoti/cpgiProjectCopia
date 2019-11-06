package calculadores;

import java.util.ArrayList;
import java.util.List;

import primitivos.Ponto;
import primitivos.Reta;
import primitivos.Retangulo;

public class RetanguloCalculador {

	public static List<Ponto> obterPontos(Retangulo retangulo) {
		return obterPontosDasRetas(obterRetas(retangulo));
	}
	
	
	private static List<Reta> obterRetas (Retangulo retangulo){
		List<Reta> retas = new ArrayList<Reta>();
		//Criando retas que formam o retangulo
		Ponto ptXminYmax = new Ponto(retangulo.getDiagonalMin().getx(), retangulo.getDiagonalMax().gety());
		Ponto ptXmaxYmin = new Ponto(retangulo.getDiagonalMax().getx(), retangulo.getDiagonalMin().gety());
		retas.add(new Reta(retangulo.getDiagonalMin(), ptXminYmax));
		retas.add(new Reta(retangulo.getDiagonalMin(), ptXmaxYmin));
		retas.add(new Reta(retangulo.getDiagonalMax(), ptXminYmax));
		retas.add(new Reta(retangulo.getDiagonalMax(), ptXmaxYmin));	
		return retas;
	}
	
	private static List<Ponto> obterPontosDasRetas(List<Reta> retas) {
		
		List<Ponto> pontos = new ArrayList<Ponto>();
		
		for (Reta reta : retas) {
			pontos.addAll(RetaCalculador.obterPontos(reta));
		}
		
		return pontos;
		
	}
}
