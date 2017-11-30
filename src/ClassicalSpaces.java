import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector ;

/*
Methodes:
- Ball (int dimension) est une fonction qui construit la boule de dimension dimension récursivement grâce à l'appel de RecursiveBall
- MatrixToFiltration(int [][] matrix) est une fonction générale qui permet à partir de la matrice (équivalente aux dessins fournis) génère la filtration
- MobiusBand,Torus, KleinBottle,  ProjectivePlane sont alors de simples fonctions qui génèrent la matrice correspondante à la géométrie (qui sera prise en entrée par MatrixToFiltration)
*/



public class ClassicalSpaces {
	
	static boolean[][] t = new boolean[9][9] ;
	
/*        Ball         */	
// La boule est créée itérativement, des qu'on ajoute un élément, 
//on relie ce dernier à tous les simplexes déjà existants dans la filtration courrante v	
	static Vector<Simplex> Ball(int dim){
		Vector<Simplex> CurrentFiltration= new Vector<Simplex>() ;
		TreeSet<Integer> tree_0 = new TreeSet<Integer>() ;
		tree_0.add(0) ; 
		CurrentFiltration.add(new Simplex(0, 0, tree_0)) ; //boule en dimension 0
		for(int i=1 ; i<dim+1 ; i++) {
			RecursiveBall(CurrentFiltration, i) ; // on l'étend jusqu'à dim dimension
		}
		return CurrentFiltration ;
	}
	
	
/*        RecursiveBall         */		
// permet d'ajouter le simplex i et de le relier avec tous les simplexes déjà existants
	static void RecursiveBall(Vector<Simplex> CurrentFiltration, int i){
		TreeSet<Integer> tree_i = new TreeSet<Integer>() ;
		tree_i.add(i) ;
		CurrentFiltration.add(new Simplex(CurrentFiltration.size(), 0, tree_i)) ;
		int length = CurrentFiltration.size() ;
		for(int j=0 ; j<length-1 ; j++){
			Simplex simplex_j = CurrentFiltration.get(j) ;
			TreeSet<Integer> t = new TreeSet<Integer>(simplex_j.vert) ;
			t.add(i) ;
			Simplex to_add_j = new Simplex(CurrentFiltration.size(), simplex_j.dim+1, t) ;
			CurrentFiltration.add(to_add_j) ;	
		}
	}
	
/*        Sphere         */	
// on a la sphère à partir de la boule de meme dimension en enlevant le simplex de plus grande dimension (ie le dernier)
	static Vector<Simplex> Sphere(int dim){
		Vector<Simplex> ball= Ball(dim);
		ball.removeElement(ball.lastElement());
		return ball;
	}
	
	
/* 			MatrixMobiusBand		*/
//représente la représentation du tore en matrice (équivalente au dessin fourni mobius_band_draw)
	static int[][] MatrixMobiusBand(){
		int[][] matrix = {
					{0, 1, 2, 3},
					{3, 4, 5, 0}
		};
		return matrix ;
		}			
	
/* 			MatrixTorus		*/
//représente la représentation du tore en matrice (équivalente au dessin fourni torus_draw)
	static int[][] MatrixTorus(){
		int[][] matrix = {
				{0, 1, 2, 0},
				{3, 4, 5, 3},
				{6, 7, 8, 6},
				{0, 1, 2, 0}
				};
		return matrix ;
	}

/* 			MatrixKleinBottle		*/
//représente la représentation de la KleinBottle en matrice (équivalente au dessin fourni klein_bottle_draw)
	static int[][] MatrixKleinBottle(){
		int[][] matrix = {
					{0, 1, 2, 0},
					{3, 4, 5, 6},
					{6, 7, 8, 3},
					{0, 1, 2, 0}
					};
		return matrix ;
	}
		
/* 			MatrixProjectivePlan		*/
//représente la représentation du ProjectivePlan en matrice (équivalente au dessin fourni klein_bottle_draw)
		static int[][] MatrixProjectivePlane(){
			int[][] matrix = {
						{0, 1, 2, 3},
						{4, 5, 6, 7},
						{7, 8, 9, 4},
						{3, 2, 1, 0}
						};
			return matrix ;
		}
	
/*        MatrixToFiltration         */		
// permet de générer la filtration pour le mobius band, le tore, la KleinBottle 
	//(on ne peut pas inclure ProjectivePlane dans cette fonction car toutes les faces ne sont pas disposées de la même manière)
	static Vector<Simplex> MatrixToFiltration(int[][] matrix){
		int l = matrix.length;
		int c = matrix[0].length;
		int f_val = 0 ; // f_val est la valeur courante à donner au prochain simplex, on l'augemente de 1 à chaque nouvel ajout
						// en travaillant d'abord sur les points, puis les arretes, puis les faces, on est sur de respecter la règle de la filtration "each simplex appears no sooner than its faces"
		
		Vector<Simplex> CurrentFiltration = new Vector<Simplex>() ;
		HashSet<TreeSet<Integer>> Triangulated = new HashSet<TreeSet<Integer>>() ; //stocke tous les simplexes déjà intégrés dans la triangluation
																				//permet de ne pas ajouter un meme point plusieurs fois (car il apparait plusieurs fois dans la matrice)
		
		// 1. ajout des points
		for(int i=0 ; i<l ; i++){
			for(int j=0 ; j<c ; j++){
				TreeSet<Integer> tree_ij = new TreeSet<Integer>() ;
				tree_ij.add(matrix[i][j]) ;
				if(!Triangulated.contains(tree_ij)){
					CurrentFiltration.add(new Simplex(f_val++, 0, tree_ij)) ;
					Triangulated.add(tree_ij) ;
				}
			}
		}
		
		//2. ajout des arrêtes, le voisins peuvent etre à droite, en bas ou en bas à droite
		for(int i=0 ; i<l ; i++){
			for(int j=0 ; j<c ; j++){
				
				// à droite
				if(j+1<c){                            //pour ne pas dépasser à droite 
					TreeSet<Integer> tree_d_ij = new TreeSet<Integer>() ;
					tree_d_ij.add(matrix[i][j]) ;					// génération de l'arrete à droite de matrix[i][j]
					tree_d_ij.add(matrix[i][j+1]) ;
					if(!Triangulated.contains(tree_d_ij)){
						CurrentFiltration.add(new Simplex(f_val++, 1, tree_d_ij)) ;
						Triangulated.add(tree_d_ij) ;
					}
				}
				
				// en bas
				if(i+1<l){								//pour ne pas dépasser en bas
					TreeSet<Integer> tree_b_ij = new TreeSet<Integer>() ;
					tree_b_ij.add(matrix[i][j]) ;					// génération de l'arrete en bas de matrix[i][j]
					tree_b_ij.add(matrix[i+1][j]) ;			
					if(!Triangulated.contains(tree_b_ij)){
						CurrentFiltration.add(new Simplex(f_val++, 1, tree_b_ij)) ;
						Triangulated.add(tree_b_ij) ;
					}
				}
				
				// en bas à droite	
				if(i+1<l && j+1<c){		//pour ne pas dépasser ni en bas, ni à droite
					TreeSet<Integer> tree_bd_ij = new TreeSet<Integer>() ;
					tree_bd_ij.add(matrix[i][j]) ;					// génération de l'arrete en bas-droite de matrix[i][j]
					tree_bd_ij.add(matrix[i+1][j+1]) ;
					if(!Triangulated.contains(tree_bd_ij)){
						CurrentFiltration.add(new Simplex(f_val++, 1, tree_bd_ij)) ;
						Triangulated.add(tree_bd_ij) ;
					}
				}
			}
		}
		
		//3. ajout des surfaces de deux types (s1 et s2)
		for(int i=0 ; i<l ; i++){
			for(int j=0 ; j<c ; j++){
				if(i+1<l && j+1<c){
					
					TreeSet<Integer> tree_s1_ij = new TreeSet<Integer>() ; // surface s1 de l'élément matrix[i][j]
					tree_s1_ij.add(matrix[i][j]) ;
					tree_s1_ij.add(matrix[i+1][j]) ;
					tree_s1_ij.add(matrix[i+1][j+1]) ;
					if(!Triangulated.contains(tree_s1_ij)){
						CurrentFiltration.add(new Simplex(f_val++, 2, tree_s1_ij)) ;
						Triangulated.add(tree_s1_ij) ;
					}
					
					
					TreeSet<Integer> tree_s2_ij = new TreeSet<Integer>() ;  //surface s2 de l'élément matrix[i][j]
					tree_s2_ij.add(matrix[i][j]) ;
					tree_s2_ij.add(matrix[i][j+1]) ;
					tree_s2_ij.add(matrix[i+1][j+1]) ;
					if(!Triangulated.contains(tree_s2_ij)){
						CurrentFiltration.add(new Simplex(f_val++, 2, tree_s2_ij)) ;
						Triangulated.add(tree_s2_ij) ;
					}
					
				}
			}
		}
		return CurrentFiltration ;	
	}
	
	
/*        MatrixToFiltrationProjectivePlane         */		
// permet de générer la filtration du projective plane à partir de leur matrice représentative 	
		static Vector<Simplex> MatrixToFiltrationProjectivePlane(int[][] matrix){
			int l = matrix.length;
			int c = matrix[0].length;
			int f_val = 0 ; // f_val est la valeur courante à donner au prochain simplex, on l'augemente de 1 à chaque nouvel ajout
							// en travaillant d'abord sur les points, puis les arretes, puis les faces, on est sur de respecter la règle de la filtration "each simplex appears no sooner than its faces"
			
			Vector<Simplex> CurrentFiltration = new Vector<Simplex>() ;
			HashSet<TreeSet<Integer>> Triangulated = new HashSet<TreeSet<Integer>>() ; //stocke tous les simplexes déjà intégrés dans la triangluation
																					//permet de ne pas ajouter un meme point plusieurs fois (car il apparait plusieurs fois dans la matrice)
			
			// 1. ajout des points, similiaire à MatrixToFiltration
			for(int i=0 ; i<l ; i++){
				for(int j=0 ; j<c ; j++){
					TreeSet<Integer> tree_ij = new TreeSet<Integer>() ;
					tree_ij.add(matrix[i][j]) ;
					if(!Triangulated.contains(tree_ij)){
						CurrentFiltration.add(new Simplex(f_val++, 0, tree_ij)) ;
						Triangulated.add(tree_ij) ;
					}
				}
			}
			
			//2. ajout des arrêtes, similaire à MatrixToFiltration à part 
			//     a) pour les éléments 2 et 7 dans notre cas il y a un edge en moins par rapport au tore 
			//     b) on rajoute un nouveau vertice (3 dans notre représentation) -> il faut prendre en compte les edge dans lesquelles ils sont impliqués
			for(int i=0 ; i<l ; i++){
				for(int j=0 ; j<c ; j++){
					if (j==c-2&&i==0) {  // cas a) élément 2 on ne prend que les arretes en bas et à droite (et non plus celle en bas-droite)
						TreeSet<Integer> tree_d_ij = new TreeSet<Integer>() ;
						tree_d_ij.add(matrix[i][j]) ;					
						tree_d_ij.add(matrix[i][j+1]) ;
						if(!Triangulated.contains(tree_d_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 1, tree_d_ij)) ;
							Triangulated.add(tree_d_ij) ;
						}
						TreeSet<Integer> tree_b_ij = new TreeSet<Integer>() ;
						tree_b_ij.add(matrix[i][j]) ;					
						tree_b_ij.add(matrix[i+1][j]) ;			
						if(!Triangulated.contains(tree_b_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 1, tree_b_ij)) ;
							Triangulated.add(tree_b_ij) ;
						}
					}
					
					if (j==0&&i==l-2) {  // cas a) élément 7 on ne prend que les arretes en bas et à droite (et non plus celle en bas-droite)
						TreeSet<Integer> tree_d_ij = new TreeSet<Integer>() ;
						tree_d_ij.add(matrix[i][j]) ;					
						tree_d_ij.add(matrix[i][j+1]) ;
						if(!Triangulated.contains(tree_d_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 1, tree_d_ij)) ;
							Triangulated.add(tree_d_ij) ;
						}
						TreeSet<Integer> tree_b_ij = new TreeSet<Integer>() ;
						tree_b_ij.add(matrix[i][j]) ;					
						tree_b_ij.add(matrix[i+1][j]) ;			
						if(!Triangulated.contains(tree_b_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 1, tree_b_ij)) ;
							Triangulated.add(tree_b_ij) ;
						}
					}
					
					if(j==0 && i==l-1) { // cas b) il s'agit du nouvel élément (noté 3) -> on prend en compte la nouvelle arrete en bas à gauche de la matrice
						TreeSet<Integer> tree_hd_ij = new TreeSet<Integer>() ;
						tree_hd_ij.add(matrix[i][j]) ;					
						tree_hd_ij.add(matrix[i-1][j+1]) ;
						if(!Triangulated.contains(tree_hd_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 1, tree_hd_ij)) ;
							Triangulated.add(tree_hd_ij) ;
						}
					}
					
					// cas général
					if(j==c-1 && i==0) { // cas b) il s'agit du nouvel élément (noté 3) -> on prend en compte la nouvelle arrete en haut à droite de la matrice
						TreeSet<Integer> tree_bg_ij = new TreeSet<Integer>() ;
						tree_bg_ij.add(matrix[i][j]) ;					
						tree_bg_ij.add(matrix[i+1][j-1]) ;
						if(!Triangulated.contains(tree_bg_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 1, tree_bg_ij)) ;
							Triangulated.add(tree_bg_ij) ;
						}
					}
					
					// à droite
					if(j+1<c && !((j==c-2&&i==0) || (j==0&&i==l-2) )){                           
						TreeSet<Integer> tree_d_ij = new TreeSet<Integer>() ;
						tree_d_ij.add(matrix[i][j]) ;					
						tree_d_ij.add(matrix[i][j+1]) ;
						if(!Triangulated.contains(tree_d_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 1, tree_d_ij)) ;
							Triangulated.add(tree_d_ij) ;
						}
					}
					
					// en bas
					if(i+1<l && !((j==c-2&&i==0) || (j==0&&i==l-2) )){							
						TreeSet<Integer> tree_b_ij = new TreeSet<Integer>() ;
						tree_b_ij.add(matrix[i][j]) ;					
						tree_b_ij.add(matrix[i+1][j]) ;			
						if(!Triangulated.contains(tree_b_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 1, tree_b_ij)) ;
							Triangulated.add(tree_b_ij) ;
						}
					}
					
					// en bas à droite	
					if(i+1<l && j+1<c && !((j==c-2&&i==0) || (j==0&&i==l-2) )){		
						TreeSet<Integer> tree_bd_ij = new TreeSet<Integer>() ;
						tree_bd_ij.add(matrix[i][j]) ;					
						tree_bd_ij.add(matrix[i+1][j+1]) ;
						if(!Triangulated.contains(tree_bd_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 1, tree_bd_ij)) ;
							Triangulated.add(tree_bd_ij) ;
						}
					}
				}
			}
			
			//3. ajout des surfaces, diffère du cas MatrixToFiltration pour le triangle en haut-droite et bas-gauche
			for(int i=0 ; i<l ; i++){
				for(int j=0 ; j<c ; j++){
					
					if (j==c-2&&i==0) {  // carré en haut à droite
						TreeSet<Integer> tree_s1_ij = new TreeSet<Integer>() ; 
						tree_s1_ij.add(matrix[i][j]) ;
						tree_s1_ij.add(matrix[i+1][j]) ;
						tree_s1_ij.add(matrix[i][j+1]) ; // ligne qui diffère
						if(!Triangulated.contains(tree_s1_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 2, tree_s1_ij)) ;
							Triangulated.add(tree_s1_ij) ;
						}
						
						
						TreeSet<Integer> tree_s2_ij = new TreeSet<Integer>() ;  
						tree_s2_ij.add(matrix[i+1][j]) ; //ligne qui diffère
						tree_s2_ij.add(matrix[i][j+1]) ;
						tree_s2_ij.add(matrix[i+1][j+1]) ;
						if(!Triangulated.contains(tree_s2_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 2, tree_s2_ij)) ;
							Triangulated.add(tree_s2_ij) ;
						}
					}
					
					if (j==0&&i==l-2 ) {  // carré en bas à gauche
						TreeSet<Integer> tree_s1_ij = new TreeSet<Integer>() ; // surface s1 de l'élément matrix[i][j]
						tree_s1_ij.add(matrix[i][j]) ;
						tree_s1_ij.add(matrix[i+1][j]) ;
						tree_s1_ij.add(matrix[i][j+1]) ; //ligne qui diffère
						if(!Triangulated.contains(tree_s1_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 2, tree_s1_ij)) ;
							Triangulated.add(tree_s1_ij) ;
						}
						
						
						TreeSet<Integer> tree_s2_ij = new TreeSet<Integer>() ;  //surface s2 de l'élément matrix[i][j]
						tree_s2_ij.add(matrix[i+1][j]) ; //ligne qui diffère
						tree_s2_ij.add(matrix[i][j+1]) ;
						tree_s2_ij.add(matrix[i+1][j+1]) ;
						if(!Triangulated.contains(tree_s2_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 2, tree_s2_ij)) ;
							Triangulated.add(tree_s2_ij) ;
						}
					}
					
					
					if(i+1<l && j+1<c && !((j==c-2&&i==0) || (j==0&&i==l-2))){
						
						TreeSet<Integer> tree_s1_ij = new TreeSet<Integer>() ; // surface s1 de l'élément matrix[i][j]
						tree_s1_ij.add(matrix[i][j]) ;
						tree_s1_ij.add(matrix[i+1][j]) ;
						tree_s1_ij.add(matrix[i+1][j+1]) ;
						if(!Triangulated.contains(tree_s1_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 2, tree_s1_ij)) ;
							Triangulated.add(tree_s1_ij) ;
						}
						
						
						TreeSet<Integer> tree_s2_ij = new TreeSet<Integer>() ;  //surface s2 de l'élément matrix[i][j]
						tree_s2_ij.add(matrix[i][j]) ;
						tree_s2_ij.add(matrix[i][j+1]) ;
						tree_s2_ij.add(matrix[i+1][j+1]) ;
						if(!Triangulated.contains(tree_s2_ij)){
							CurrentFiltration.add(new Simplex(f_val++, 2, tree_s2_ij)) ;
							Triangulated.add(tree_s2_ij) ;
						}
						
					}
				}
			}
			return CurrentFiltration ;	
		}
		

/*          ExportFiltrations          */
// Exporte le Vector<Simplex> Filtration dans un fichier nommé FileName (sous la structure adpatée:  f_val dim vect1 vect2 vect3)
		static void ExportFiltration(String FileName, Vector<Simplex> Filtration) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(FileName));
			int n = Filtration.size();
			for(int j=0;j<n;j++){
				Simplex current=Filtration.get(j);
				writer.println(current.toString_Filtration());
			}
			writer.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}	

/*          main         */
//exporte all the filtrations asked in the TD 5! 
		public static void main(String[] args){
			for (int d=0;d<11;d++) {
				String FileName = "ball_"+d+".txt";
				Vector<Simplex> Filtration_b = Ball(d);
				ExportFiltration(FileName, Filtration_b);
			}
			for (int d=0;d<11;d++) {
				String FileName = "sphere_"+d+".txt";
				Vector<Simplex> Filtration_s = Sphere(d);
				ExportFiltration(FileName, Filtration_s);
			}
			
			for (int i=0;i<4;i++) {
				String FileName ="";
				Vector<Simplex> Filtration =null;
				if (i==0) {
					FileName = "mobius_band.txt";
					Filtration = MatrixToFiltration(MatrixMobiusBand());
				}
				if (i==1) {
					FileName = "torus.txt";
					Filtration = MatrixToFiltration(MatrixTorus());
				}
				if (i==2) {
					FileName = "klein_bottle.txt";
					Filtration = MatrixToFiltration(MatrixKleinBottle());
				}
				if (i==3) {
					FileName = "projective_plane.txt";
					Filtration =  MatrixToFiltrationProjectivePlane(MatrixProjectivePlane());
				}
				ExportFiltration(FileName, Filtration);
		}
	}

}
	

