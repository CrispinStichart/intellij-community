// "Qualify static 'rr' access with reference to class 'R'|->Extract possible side effects" "true-preview"

class AClass
{
    public static class R {
        static int rr = 0;
    }
    public R getR() {
        return null;
    }
}
class ss {
    void f(AClass d){
        int i = <caret>d.getR().rr;
    }

}
