#include<stdio.h>

#include<stdlib.h>
const long long N_base=3037000499LL;
const long long N_maxdgt=-1LL+N_base;
//==============================================================================
    static struct NODEi64{
        struct NODEi64* l;
        struct NODEi64* r;
        long long v;
    };
    const static size_t sizeof$NODEi64$=8+sizeof(void*)*2;

    static void free$NODEi64$(struct NODEi64* n);
    //==========================================================================
    static void free$NODEi64$(struct NODEi64* n){
        struct NODEi64* r;
        while(n){
            r=n->r;
            free(n);
            n=r;
        }
    }
//==============================================================================
    struct N{
        struct NODEi64  n0;
        struct NODEi64* np;
    };
    const size_t sizeof$N$=sizeof$NODEi64$+sizeof(void*);

    struct N* N(long long v);
    void free$N$(struct N* o);

    void          N_replc(struct N* x,struct N* y);
    struct N*     N_clone(struct N* v);
    unsigned char N_equiv(struct N* x,struct N* y);
    short         N_order(struct N* x,struct N* y);
    void          N_setto(struct N* o,struct N* v);

    struct N*     N_add         (struct N* x,struct N* y);
    void          N_addby       (struct N* x,struct N* y);
    void          N_addby1      (struct N* x);
    void          N_addbybase   (struct N* x);

    struct N*     N_minus       (struct N* x,struct N* y);
    void          N_minusby     (struct N* x,struct N* y);
    void          N_minusby1    (struct N* x);
    void          N_minusbybase (struct N* x);

    void          N_multbybase  (struct N* x);
    struct N*     N_multi64     (struct N* x,long long y);
    struct N*     N_mult        (struct N* x,struct N* y);
    void          N_multby      (struct N* x,struct N* y);

    unsigned char N_divbybase   (struct N* m);
    void          N_divby2      (struct N* m);
    struct N*     N_div         (struct N* m,struct N* n);
    unsigned char N_divisibility(struct N* q);
    unsigned char N_divby       (struct N* m,struct N* n);
    struct N*     N_mod         (struct N* m,struct N* n);
    //==========================================================================
    struct N* N(long long v){
        struct N* o=malloc(sizeof$N$);
        long long q=v/N_base;
        (o->np=&o->n0)->l=NULL;
        o->n0.v=v-N_base*q;
        while(v=q)
            (o->np=((o->np->r=malloc(sizeof$NODEi64$))->l=o->np)->r)->v=v-N_base*(q=v/N_base);
        o->np->r=NULL;
        return o;
    }
    void free$N$(struct N* o){
        free$NODEi64$(o->n0.r);
        free(o);
    }

    void          N_replc(struct N* x,struct N* y){
        free$NODEi64$(x->n0.r);//deletable
        struct NODEi64* n=(*x=*y).n0.r;
        if(n)n->l=&x->n0;
        else x->np=&x->n0;
        free(y);//deletable
    }
    struct N*     N_clone(struct N* v){
        struct N* o=malloc(sizeof$N$);
        (o->np=&o->n0)->l=NULL;
        struct NODEi64* n=&v->n0;
        o->n0.v=n->v;
        while(n=n->r)
            (o->np=((o->np->r=malloc(sizeof$NODEi64$))->l=o->np)->r)->v=n->v;
        o->np->r=NULL;
        return o;
    }
    unsigned char N_equiv(struct N* x,struct N* y){
        struct NODEi64* nx=&x->n0;
        struct NODEi64* ny=&y->n0;
        do  if(nx->v!=ny->v)
                return 0;
        while(
            (nx=nx->r)!=NULL&
            (ny=ny->r)!=NULL
        );
        return nx==ny;
    }
    short         N_order(struct N* x,struct N* y){
        struct NODEi64* nx=&x->n0;
        struct NODEi64* ny=&y->n0;
        long long d=0LL;
        do  if(nx->v!=ny->v)
                d=nx->v-ny->v;
        while(
            (nx=nx->r)!=NULL&
            (ny=ny->r)!=NULL
        );
        if(nx)return 1;
        if(ny)return-1;
        if(d<0LL)return-1;
        if(d>0LL)return 1;
        return 0;
    }
    void          N_setto(struct N* o,struct N* v){
        struct NODEi64* no=&o->n0;
        struct NODEi64* nv=&v->n0;
        struct NODEi64* t;
        do  (t=no)->v=nv->v;
        while(
            (no=no->r)!=NULL&
            (nv=nv->r)!=NULL
        );
        while(nv){
            (t=o->np=((o->np->r=malloc(sizeof$NODEi64$))->l=o->np)->r)->v=nv->v;
            nv=nv->r;
        }
        o->np->r=NULL;free$NODEi64$(t->r);//deletable
        (o->np=t)->r=NULL;
    }

    struct N*     N_add         (struct N* x,struct N* y){
        struct N* z=malloc(sizeof$N$);
        struct NODEi64* nx=&x->n0;
        struct NODEi64* ny=&y->n0;
        long long dgt=nx->v+ny->v;
        long long q=dgt/N_base;
        (z->np=&z->n0)->l=NULL;
        z->n0.v=dgt-N_base*q;
        while(
            (nx=nx->r)!=NULL&
            (ny=ny->r)!=NULL
        ){  q=(dgt=q+
                nx->v+
                ny->v
            )/N_base;
            (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=dgt-N_base*q;
        }
        while(nx){
            q=(dgt=q+
                nx->v
            )/N_base;
            (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=dgt-N_base*q;
            nx=nx->r;
        }
        while(ny){
            q=(dgt=q+
                ny->v
            )/N_base;
            (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=dgt-N_base*q;
            ny=ny->r;
        }
        if(q)
            (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=1LL;
        z->np->r=NULL;
        return z;
    }
    void          N_addby       (struct N* x,struct N* y){
        struct NODEi64* nx=&x->n0;
        struct NODEi64* ny=&y->n0;
        long long dgt;
        long long q=0LL;
        do{ q=(dgt=q+
                nx->v+
                ny->v
            )/N_base;
            nx->v=dgt-N_base*q;
        }while(
            (nx=nx->r)!=NULL&
            (ny=ny->r)!=NULL
        );
        while(nx){
            q=(dgt=q+
                nx->v
            )/N_base;
            nx->v=dgt-N_base*q;
            nx=nx->r;
        }
        while(ny){
            q=(dgt=q+
                ny->v
            )/N_base;
            (x->np=((x->np->r=malloc(sizeof$NODEi64$))->l=x->np)->r)->v=dgt-N_base*q;
            ny=ny->r;
        }
        if(q)
            (x->np=((x->np->r=malloc(sizeof$NODEi64$))->l=x->np)->r)->v=1LL;
        x->np->r=NULL;
    }
    void          N_addby1      (struct N* x){
        struct NODEi64* n=&x->n0;
        long long q=1LL;
        do  if(n->v==N_maxdgt){
                n->v=0LL;
                n=n->r;
            }else{
                ++n->v;
                q=0LL;
            }
        while(q!=0LL&n!=NULL);
        if(q){
            (x->np=((x->np->r=malloc(sizeof$NODEi64$))->l=x->np)->r)->v=1LL;
             x->np->r=NULL;
        }
    }
    void          N_addbybase   (struct N* x){
        struct NODEi64* n=x->n0.r;
        if(n){
            long long q=1LL;
            do  if(n->v==N_maxdgt){
                    n->v=0LL;
                    n=n->r;
                }else{
                    ++n->v;
                    q=0LL;
                }
            while(q!=0LL&n!=NULL);
            if(q){
                (x->np=((x->np->r=malloc(sizeof$NODEi64$))->l=x->np)->r)->v=1LL;
                 x->np->r=NULL;
            }
        }else{  (x->np=((x->np->r=malloc(sizeof$NODEi64$))->l=x->np)->r)->v=1LL;
                 x->np->r=NULL;
        }
    }

    struct N*     N_minus       (struct N* x,struct N* y){
        struct N* z=malloc(sizeof$N$);
        struct NODEi64* nx=&x->n0;
        struct NODEi64* ny=&y->n0;
        long long dgt=nx->v-ny->v;
        long long q=(N_base+dgt)/N_base-1LL;
        struct NODEi64* t=z->np=&z->n0;
        t->l=NULL;
        t->v=dgt-N_base*q;
        nx=nx->r;
        while((ny=ny->r)){
            q=(N_base+(dgt=q+
                nx->v-
                ny->v
            ))/N_base-1LL;
            if(
                (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=dgt-N_base*q
            )t=z->np;
            nx=nx->r;
        }
        while(nx){
            q=(N_base+(dgt=q+
                nx->v
            ))/N_base-1LL;
            if(
                (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=dgt-N_base*q
            )t=z->np;
            nx=nx->r;
        }
        z->np->r=NULL;free$NODEi64$(t->r);//deletable
        (z->np=t)->r=NULL;
        return z;
    }
    void          N_minusby     (struct N* x,struct N* y){
        struct NODEi64* nx=&x->n0;
        struct NODEi64* ny=&y->n0;
        long long dgt;
        long long q=0LL;
        struct NODEi64* t=nx;
        do{ q=(N_base+(dgt=q+
                nx->v-
                ny->v
            ))/N_base-1LL;
            if(nx->v=dgt-N_base*q)
                t=nx;
            nx=nx->r;
        }while(ny=ny->r);
        while(nx){
            q=(N_base+(dgt=q+
                nx->v
            ))/N_base-1LL;
            if(nx->v=dgt-N_base*q)
                t=nx;
            nx=nx->r;
        }
        free$NODEi64$(t->r);//deletable
        (x->np=t)->r=NULL;
    }
    void          N_minusby1    (struct N* x){
        struct NODEi64* n=&x->n0;
        long long q=-1LL;
        do  if(n->v){
                --n->v;
                q=0LL;
            }else{
                n->v=N_maxdgt;
                n=n->r;
            }
        while(q);
        if(x->np->v==0LL&(n=x->np->l)!=NULL){
            free(n->r);//deletable
            (x->np=n)->r=NULL;
        }
    }
    void          N_minusbybase (struct N* x){
        struct NODEi64* n=x->n0.r;
        long long q=-1LL;
        do  if(n->v){
                --n->v;
                q=0LL;
            }else{
                n->v=N_maxdgt;
                n=n->r;
            }
        while(q);
        if(x->np->v==0LL&(n=x->np->l)!=NULL){
            free(n->r);//deletable
            (x->np=n)->r=NULL;
        }
    }

    void          N_multbybase  (struct N* x){
        struct NODEi64* n0=&x->n0;
        struct NODEi64* n1=n0->r;

        /*if(n1!=NULL|n0->v!=0LL){
            struct NODEi64* nn=malloc(sizeof$NODEi64$);
            nn->v=((n0->r=nn)->l=n0)->v;
            n0->v=0LL;
            if(nn->r=n1)n1->l=nn;
            else        x->np=nn;
        }*/

        if(n1){
            struct NODEi64* nn=malloc(sizeof$NODEi64$);
            nn->v=((n0->r=nn)->l=n0)->v;
            n0->v=0LL;
            (nn->r=n1)->l=nn;
        }else if(n0->v){
            struct NODEi64* nn=malloc(sizeof$NODEi64$);
            nn->v=((n0->r=nn)->l=n0)->v;
            n0->v=0LL;
            (x->np=nn)->r=n1;
        }
    }
    struct N*     N_multi64     (struct N* x,long long y){
        struct N* z=malloc(sizeof$N$);
        (z->np=&z->n0)->l=NULL;
        if(y){
            struct NODEi64* n=&x->n0;
            long long dgt=n->v*y;
            long long q=dgt/N_base;
            z->n0.v=dgt-N_base*q;
            while(n=n->r){
                q=(dgt=q+n->v*y)/N_base;
                (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=dgt-N_base*q;
            }
            if(q)
                (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=q;
        }else
            z->n0.v=0LL;
        z->np->r=NULL;
        return z;
    }
    struct N*     N_mult        (struct N* x,struct N* y){

        /*// definition of multiplication of polynomials
        struct N* z=malloc(sizeof$N$);
        (z->np=&z->n0)->v=0LL;
                z->n0 . l=
                z->n0 . r=NULL;
        struct NODEi64* n=&y->n0;
        struct N* xbp=N_clone(x);
        struct N* xbpy;
        do{ N_addby(z,xbpy=N_multi64(xbp,n->v));
            free$N$(xbpy);//deletable
            N_multbybase(xbp);
        }while(n=n->r);
        free$N$(xbp);//deletable
        return z;*/

        /*// N_multbybase(xbp) is done less by one time than previous version
        struct N* z=malloc(sizeof$N$);
        (z->np=&z->n0)->v=0LL;
                z->n0 . l=
                z->n0 . r=NULL;
        struct NODEi64* n=&y->n0;
        struct N* xbp=N_clone(x);
        struct N* xbpy;
        N_addby(z,xbpy=N_multi64(xbp,n->v));
        free$N$(xbpy);//deletable
        while(n=n->r){
            N_multbybase(xbp);
            N_addby(z,xbpy=N_multi64(xbp,n->v));
            free$N$(xbpy);//deletable
        }
        free$N$(xbp);//deletable
        return z;*/

        // simplified N_multbybase(xbp) than previous version
        /*struct N* z=malloc(sizeof$N$);
        (z->np=&z->n0)->v=0LL;
                z->n0 . l=
                z->n0 . r=NULL;
        if(x->n0.r!=NULL|x->n0.v!=0LL){
            struct NODEi64* n=&y->n0;
            struct N* xbp=N_clone(x);
            struct N* xbpy;
            N_addby(z,xbpy=N_multi64(xbp,n->v));
            free$N$(xbpy);//deletable
            struct NODEi64* n0=&xbp->n0;
            struct NODEi64* n1;
            struct NODEi64* nn;
            while(n=n->r){
                n1=n0->r;
                ((n0->r=nn=malloc(sizeof$NODEi64$))->l=n0)->r->v=n0->v;
                n0->v=0LL;
                if(n1)(nn->r=n1)->l=nn;
                else(xbp->np=nn)->r=n1;
                N_addby(z,xbpy=N_multi64(xbp,n->v));
                free$N$(xbpy);//deletable
            }
            free$N$(xbp);//deletable
        }
        return z;*/

        if(x->n0.r){
            struct N* z=malloc(sizeof$N$);
            (z->np=&z->n0)->v=0LL;
                    z->n0 . l=
                    z->n0 . r=NULL;
            struct NODEi64* n=&y->n0;
            struct N* xbp=N_clone(x);
            struct N* xbpy;
            N_addby(z,xbpy=N_multi64(xbp,n->v));
            free$N$(xbpy);//deletable
            struct NODEi64* n0=&xbp->n0;
            struct NODEi64* n1;
            struct NODEi64* nn;
            while(n=n->r){
                n1=n0->r;
                nn=malloc(sizeof$NODEi64$);
                nn->v=((n0->r=nn)->l=n0)->v;
                n0->v=0LL;
                (nn->r=n1)->l=nn;
                N_addby(z,xbpy=N_multi64(xbp,n->v));
                free$N$(xbpy);//deletable
            }
            free$N$(xbp);//deletable
            return z;
        }
        if(x->n0.v)
            return N_multi64(y,x->n0.v);
        struct N* z=malloc(sizeof$N$);
        (z->np=&z->n0)->v=0LL;
                z->n0 . l=
                z->n0 . r=NULL;
        return z;
    }
    void          N_multby      (struct N* x,struct N* y){
        N_replc(x,N_mult(x,y));
    }

    unsigned char N_divbybase   (struct N* m){
        unsigned char d=m->n0.v==0LL;
        struct NODEi64* n0=&m->n0;
        struct NODEi64* n1=n0->r;
        if(n1){
            n0->v=n1->v;
            struct NODEi64* n2=n1->r;
            if(n0->r=n2)n2->l=n0;
            else        m->np=n0;
            free(n1);//deletable
        }else n0->v=0LL;
        return d;
    }
    void          N_divby2      (struct N* m){
        struct NODEi64* n=m->np;
        long long r=0LL;
        long long dgt;
        do{ n->v=dgt=(r=N_base*r+n->v)/2LL;
            r-=dgt*2LL;
        }while(n=n->l);
        if(m->np->v==0LL&(n=m->np->l)!=NULL){
            m->np=n;
            free(n->r);//deletable
            n->r=NULL;
        }
    }
    struct N*     N_div         (struct N* m,struct N* n){
        struct N*  q=malloc(sizeof$N$);
        ( q->np=& q->n0)->v=0LL;
                  q->n0 . l=
                  q->n0 . r=NULL;
        struct N* nq=malloc(sizeof$N$);
        (nq->np=&nq->n0)->v=0LL;
                 nq->n0 . l=
                 nq->n0 . r=NULL;
        long long order=N_order(nq,m);

        struct N*  q_step=N_clone(m);
        struct N* nq_step;
        while(q_step->n0.r!=NULL|q_step->n0.v>1LL){
            if(order<0LL){
                N_addby  ( q, q_step);
                N_addby  (nq,nq_step=N_mult(n,q_step));
            }else if(order>0LL){
                N_minusby( q, q_step);
                N_minusby(nq,nq_step=N_mult(n,q_step));
            }else{
                free$N$(q_step);//deletable
                free$N$(nq);//deletable
                return q;
            }
            free$N$(nq_step);//deletable
            N_divby2(q_step);
            order=N_order(nq,m);
        }
        free$N$(q_step);//deletable
        while(order<0LL){
            N_addby1  ( q);
            N_addby   (nq,n);
            order=N_order(nq,m);
        }
        while(order>0LL){
            N_minusby1( q);
            N_minusby (nq,n);
            order=N_order(nq,m);
        }
        free$N$(nq);//deletable
        q->n0.l=(void*)-order; // divisible ? -order=-0=0=NULL : -order=-(-1)=1
        return q;
    }
    unsigned char N_divisibility(struct N* q){
        unsigned char d=q->n0.l==NULL;
        q->n0.l=NULL;
        return d;
    }
    unsigned char N_divby       (struct N* m,struct N* n){
        struct N* q=N_div(m,n);
        unsigned char d=N_divisibility(q);
        N_replc(m,q);
        return d;
    }
    struct N*     N_mod         (struct N* m,struct N* n){
        struct N*  q=N_div(m,n);
        if(N_divisibility(q)){
            free$N$(q);//deletable
            q=malloc(sizeof$N$);
            (q->np=&q->n0)->v=0LL;
                    q->n0 . l=
                    q->n0 . r=NULL;
            return q;
        }
        struct N* nq=N_mult(n,q);
        free$N$( q);//deletable
        q=N_minus(m,nq);
        free$N$(nq);//deletable
        return q;
    }







    void printN(struct N* o){
        struct NODEi64* n=o->np;
        printf("%lld",n->v);
        while(n=n->l)
            printf(",%lld",n->v);
    }
    void printlnN(struct N* o){
        printN(o);
        printf("\n");
    }
//==============================================================================