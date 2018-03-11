#include <iostream>
#include <vector>
#include <algorithm>
#include <set>
using namespace std;

class Graph{
private:
public:
        int V,E;
        // edge list format - (w,x,y)
        vector<pair<int,pair<int,int> > > edges;
        vector<pair<int,pair<int,int> > > MST;
		double MST_weight;
        Graph(int V,int E);
        void addEdge(int u,int v,int w);
        void removeEdge(int u, int v);
        void modifyWeight(int u, int v, int w);
        int kruskalMST();
        void printMST();
        bool containsCycle(bool visitedArray[]);
        bool isTree();
        bool isConnected();        
};
Graph::Graph(int V,int E){
    this->V = V;
    this->E = E;
}
void Graph::addEdge(int u,int v,int w){
    edges.push_back({w,{u,v}});
}

void Graph::removeEdge(int u, int v){
	vector<pair<int,pair<int,int> > >::iterator it;
    for(it = edges.begin();it!=edges.end();it++){
    	if((it->second.first==u && it->second.second == v) || (it->second.first==v && it->second.second == u)){
    		edges.erase(it);
    		break;
    	}
    }
    E--;
}

void Graph::modifyWeight(int u, int v, int w){
	vector<pair<int,pair<int,int> > >::iterator it;
    for(it = edges.begin();it!=edges.end();it++){
    	if((it->second.first==u && it->second.second == v) || (it->second.first==v && it->second.second == u)){
    		it->first = w;
    		break;
    	}
    }
}

void Graph::printMST(){
    vector<pair<int,pair<int,int> > >::iterator it;
    for(it = MST.begin();it!=MST.end();it++){
        cout << it->second.first << " - " << it->second.second << endl;
    }
}

bool Graph::containsCycle(bool visitedArray[]){	
	for(auto edge : edges){
		int u = edge.second.first;
		int v = edge.second.second;
		if(visitedArray[u] && visitedArray[v])
			return true;
		visitedArray[u] = true;
		visitedArray[v] = true;
	}
	return false;
}

bool Graph::isTree(){
	bool *visitedArray = new bool[V];
	for(int i=0; i<V; i++){
		visitedArray[i]=false;
	}

	bool cycle = containsCycle(visitedArray);
	cout << "is cycle "<< cycle << endl;
	if(cycle)
		return false;

	for(int i=0; i<V; i++){
		// cout << i << " is visited " << visitedArray[i] << endl;;
		if(!visitedArray[i])
			return false;
	}
	return true;
}

bool Graph::isConnected(){
	bool *visitedArray = new bool[V];
	for(int i=0; i<V; i++){
		visitedArray[i]=false;
	}

	for(auto edge : edges){
		int u = edge.second.first;
		int v = edge.second.second;
		visitedArray[u] = true;
		visitedArray[v] = true;
	}

	for(int i=0; i<V; i++){
		// cout << i << " is visited " << visitedArray[i] << endl;;
		if(!visitedArray[i])
			return false;
	}
	return true;
}

struct DisjointSet{
    int *parent,*rnk;
    int n;

    DisjointSet(int n){
        this->n = n;
        parent = new int[n+1];
        rnk = new int[n+1];

        for(int i=0;i<=n;i++){
            rnk[i] = 0;
            parent[i] = i;
        }
    }
    int Find(int u){
        if(u == parent[u]) return parent[u];
        else return Find(parent[u]);
    }

    void Union(int x,int y){
        x = Find(x);
        y = Find(y);
        if(x != y){
            if(rnk[x] < rnk[y]){
                rnk[y] += rnk[x];
                parent[x] = y;
            }
            else{
                rnk[x] += rnk[y];
                parent[y] = x;
            }
        }
    }
};
int Graph::kruskalMST(){
    int MSTWeight = 0; //sum of all vertex weights
    sort(edges.begin(),edges.end());
    //for all u in G_v
    //    MAKE-SET(u)
    DisjointSet ds(this->V);

    vector<pair<int,pair<int,int> > >::iterator it;
    // for all edges in G
    for(it = edges.begin(); it!=edges.end();it++){
        int u = it->second.first;
        int v = it->second.second;

        int setU = ds.Find(u);
        int setV = ds.Find(v);


        if(setU != setV){
            int w = it->first;
            MST.push_back({w,{u,v}});
            MSTWeight += it->first;

            ds.Union(setU,setV);
        }
    }
    MST_weight = MSTWeight;
    return MSTWeight;
}

struct Graph_compare{
bool operator() (const Graph& lhs, const Graph& rhs)const{
	return lhs.MST_weight<rhs.MST_weight;
	}
};

void printMST(Graph g){
    vector<pair<int,pair<int,int> > >::iterator it;
    for(it = g.MST.begin();it!=g.MST.end();it++){
        cout << it->second.first << " - " << it->second.second << endl;
    }
}


int main(){
    int V,E;
    cout << "Number of vertices: "<< endl;
    cin>>V;
    cout << "Number of edges: "<< endl;
    cin>>E;
    Graph g(V,E);
    int u,v,w;
    for(int i=0;i<E;i++){
        cin >> u >> v >> w;
        g.addEdge(u,v,w);
    }

    set<Graph, Graph_compare> partitions; 
    vector<Graph> answer; 

    if(g.isConnected()){
	    double weight = g.kruskalMST();	    

	    partitions.insert(g);
	    // create partitions and stuff
	    // Use a priority queue
	    while(!partitions.empty()){
	    	// Poll
	    	auto smallest = partitions.begin(); 
	    	partitions.erase(smallest);
	    	// to output kth largest
	    	// answer.push_back(*smallest);

	    	weight = smallest->MST_weight;
	    	cout << "MST: " << endl;
	    	printMST(*smallest);
	    	cout << "Weight of MST is: " << weight << endl;

	    	// generate the new partitions using this partition
	    	int i = 0;
    		for(auto edge: smallest->edges){
    			if(edge.first==0)
    				i++;
    			else
    				break;
    		}
	    	for(int j=0; j<smallest->V-1-i; j++){
	    		// create deep copy
	    		Graph part(smallest->V, smallest->E);
	    		part.edges = smallest->edges;
	    		// add/remove edges accordingly

	    		// the edges that we need to include -> make their weight 0 -> equivalent
	    		for(int z=0; z<j; z++){
	    			auto edge = part.edges.begin();
	    			edge->first=0;
	    		}

	    		// the edges that we need to exclude -> remove them from graph
	    		int z = 0;
	    		vector<pair<int,pair<int,int> > >::iterator edge;
	    		for(edge = part.edges.begin(); edge!=part.edges.end(); edge++){
	    			if(z==i+j){
	    				part.edges.erase(edge);
	    				break;
	    			}
	    			z++;
	    		}
	    		if(part.isConnected()){
	    			part.kruskalMST();
	    			if(part.MST.size()<part.V-1)
	    				continue;
	    			partitions.insert(part);
	    		}
	    		else
	    			continue;

	    	}
	    }
	}
	else
		cout << "Not a connected graph" << endl;
    return 0;
}


