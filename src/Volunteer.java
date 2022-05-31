import java.util.*;
import java.io.*;

public class Volunteer<T>  {

    public T last;
    public T first;
    public T email;

    Volunteer(T last, T first, T email) {  
        this.last = last;
        this.first = first;
        this.email = email;
    }

    public T getLast() {
        return last;
    }

    public T getFirst() {
        return first;
    }

    public T getEmail() {
        return email;
    }

    public String toString() {
        return  "Do you want to delete "+first+" "+last+", "+email+"?";
    }
}