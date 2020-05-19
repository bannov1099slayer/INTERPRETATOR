public final class Token {
    private terminals type;
    private String text;//хранит имя переменной
    public Token(){

    }
    public Token(terminals type, String text ){
        this.type = type;
        this.text = text;

    }

    public terminals getType() {
        return type;
    }

    public void setType(terminals type) {

        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {

        this.text = text;
    }

    public String toString(){
        return type +" " + text ;
    }
}
